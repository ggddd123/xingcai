package com.xingcai.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingcai.content.model.dto.CourseCategoryTreeDto;
import com.xingcai.content.model.po.CourseCategory;

import java.util.List;


public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    //使用递归查询
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);

}
