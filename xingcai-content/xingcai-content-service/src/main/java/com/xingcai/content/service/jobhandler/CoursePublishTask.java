package com.xingcai.content.service.jobhandler;


import com.xingcai.base.exception.CustomException;
import com.xingcai.content.feignclient.SearchServiceClient;
import com.xingcai.content.mapper.CoursePublishMapper;
import com.xingcai.content.model.po.CoursePublish;
import com.xingcai.content.service.CoursePublishService;
import com.xingcai.messagesdk.model.po.MqMessage;
import com.xingcai.messagesdk.service.MessageProcessAbstract;
import com.xingcai.messagesdk.service.MqMessageService;
import com.xingcai.search.po.CourseIndex;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @XxlJob("CoursePubishJobHandler")
    public void coursePubishJobHandler(){
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号
        int shardTotal = XxlJobHelper.getShardTotal();//执行器的总数
        process(shardIndex,shardTotal,"course_publish",30,60);
    }


    @Override
    public boolean execute(MqMessage mqMessage) {

        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());


        //向redis写缓存
        saveCourseCache(mqMessage,courseId);
        //向es写索引数据
        saveCourseIndex(mqMessage,courseId);
        //课程静态化上传至minio
        generateCourseHtml(mqMessage,courseId);
        return true;
    }
    //生成课程静态化页面
    private void generateCourseHtml(MqMessage mqMessage,Long courseId){
        //做任务幂等性处理
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息id
        Long id = mqMessage.getId();
        int stageThree = mqMessageService.getStageThree(id);
        if(stageThree>0){
            log.debug("课程静态化完成，无需处理");
            return;
        }
        //生成HTML页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if(file==null){
            CustomException.cast("生成的静态文件为空");
        }

        //将页面上传到minio
        coursePublishService.uploadCourseHtml(courseId,file);


        mqMessageService.completedStageThree(id);
    }

    //向es写索引数据
    private void saveCourseIndex(MqMessage mqMessage,Long courseId){
        //做任务幂等性处理
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息id
        Long id = mqMessage.getId();
        int stageTwo = mqMessageService.getStageTwo(id);
        if(stageTwo>0){
            log.debug("课程索引信息添加完成，无需处理");
            return;
        }

        //查询课程数据，调用搜索服务添加搜索接口
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);

        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            CustomException.cast("远程调用搜索服务，添加课程失败");
        }

        mqMessageService.completedStageTwo(id);
    }

    public void saveCourseCache(MqMessage mqMessage,long courseId){
        //做任务幂等性处理
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息id
        Long id = mqMessage.getId();
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne>0){
            log.debug("课程缓存信息添加完成，无需处理");
            return;
        }
        mqMessageService.completedStageOne(id);


    }





}
