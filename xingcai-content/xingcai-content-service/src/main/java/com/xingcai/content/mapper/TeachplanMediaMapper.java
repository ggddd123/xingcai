package com.xingcai.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingcai.content.model.po.TeachplanMedia;

import java.util.List;


public interface TeachplanMediaMapper extends BaseMapper<TeachplanMedia> {


    //根据课程计划ID删除媒资信息
    public TeachplanMedia deleteByTeachplanId(Long teachplan_id);


    //根据根据课程计划ID查找媒资信息
    public List<TeachplanMedia> selectByTeachplanId(Long teachplan_id);


}
