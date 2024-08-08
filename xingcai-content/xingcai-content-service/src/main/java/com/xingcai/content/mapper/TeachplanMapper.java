package com.xingcai.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingcai.content.model.dto.TeachplanDto;
import com.xingcai.content.model.po.Teachplan;

import java.util.List;


public interface TeachplanMapper extends BaseMapper<Teachplan> {


    //课程计划查询
    public List<TeachplanDto> selectTreeNodes(Long courseId);


    //根据parentid查询
    public List<Teachplan> selectByParentId(Long id);


}
