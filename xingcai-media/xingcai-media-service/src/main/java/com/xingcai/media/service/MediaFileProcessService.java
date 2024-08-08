package com.xingcai.media.service;

import com.xingcai.media.model.po.MediaProcess;

import java.util.List;

public interface MediaFileProcessService {

    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    public boolean startTask(long id);

    //保存任务结果
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
