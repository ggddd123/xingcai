package com.xingcai.messagesdk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingcai.messagesdk.model.po.MqMessage;

import java.util.List;


public interface MqMessageService extends IService<MqMessage> {

    /**
     * @description 扫描消息表记录，采用与扫描视频处理表相同的思路
     */
    public List<MqMessage> getMessageList(int shardIndex, int shardTotal,  String messageType,int count);

    /**
     * @description 添加消息
    */
    public MqMessage addMessage(String messageType,String businessKey1,String businessKey2,String businessKey3);
    /**
     * @description 完成任务
     */
    public int completed(long id);

    /**
     * @description 完成阶段任务
     */
    public int completedStageOne(long id);
    public int completedStageTwo(long id);
    public int completedStageThree(long id);
    public int completedStageFour(long id);

    /**
     * @description 查询阶段状态
    */
    public int getStageOne(long id);
    public int getStageTwo(long id);
    public int getStageThree(long id);
    public int getStageFour(long id);

}
