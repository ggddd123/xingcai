package com.xingcai.media.service.jobhandler;

import com.xingcai.base.utils.Mp4VideoUtil;
import com.xingcai.media.model.po.MediaProcess;
import com.xingcai.media.service.MediaFileProcessService;
import com.xingcai.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 */
@Component
@Slf4j
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;


    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;


    private static Logger logger = LoggerFactory.getLogger(VideoTask.class);


    /*
     * 发片广播
     * */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号
        int shardTotal = XxlJobHelper.getShardTotal();//执行器的总数

        //确定CPU的核心数
        int i = Runtime.getRuntime().availableProcessors();
        //查询待处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, i);
        int size = mediaProcessList.size();
        log.debug("取到的视频处理任务数:" + size);
        if (size == 0) {
            return;
        }
        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);

        for (MediaProcess mediaProcess : mediaProcessList) {
            try {


                executorService.execute(() -> {
                    //任务执行
                    Long id = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();
                    //开启任务
                    boolean b = mediaFileProcessService.startTask(id);
                    if (!b) {
                        log.debug("抢占任务失败");
                        return;
                    }

                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    //执行视频的转码
                    File file = mediaFileService.downloadFileFromMinIo(bucket, objectName);
                    if (file == null) {
                        log.debug("下载视频出错");
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "下载视频出错");
                        return;
                    }


                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常");
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    //转换后mp4文件的路径
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    String s = videoUtil.generateMp4();
                    if (!s.equals("success")) {
                        log.debug("视频转码失败");
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "视频转码失败");
                    }
                    //上传到minio
                    objectName=getFilePath(fileId,".mp4");
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, objectName);
                    if (!b1) {
                        log.debug("上传至minion失败");
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", fileId, null, "上传至minion失败");
                        return;

                    }

                    String filePath = getFilePath(fileId, ".mp4");

                    //保存视频的结果
                    mediaFileProcessService.saveProcessFinishStatus(id, "2", fileId, filePath, s);


                });
            } finally {
                countDownLatch.countDown();
            }
        }
        //阻塞，指定最大限度的等待时间
        countDownLatch.await(30, TimeUnit.MINUTES);


    }


    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + fileExt;
    }


}


