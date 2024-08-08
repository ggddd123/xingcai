package com.xingcai.content.service;

import com.xingcai.base.model.PageParams;
import com.xingcai.base.model.PageResult;
import com.xingcai.content.model.dto.AddCourseDto;
import com.xingcai.content.model.dto.CourseBaseInfoDto;
import com.xingcai.content.model.dto.EditCourseDto;
import com.xingcai.content.model.dto.QueryCourseParamsDto;
import com.xingcai.content.model.po.CourseBase;

public interface CourseBaseInfoService {

    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    public CourseBaseInfoDto getCourseBaseInfoDto(Long id);

    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    void deleteCourse(Long courseId);
}
