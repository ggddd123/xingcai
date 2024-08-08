package com.xingcai.learning.service;

import com.xingcai.base.model.PageResult;
import com.xingcai.learning.model.dto.MyCourseTableParams;
import com.xingcai.learning.model.dto.XcChooseCourseDto;
import com.xingcai.learning.model.dto.XcCourseTablesDto;
import com.xingcai.learning.model.po.XcCourseTables;


public interface MyCourseTablesService {


    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);


    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    public PageResult<XcCourseTables> mycourestabls(MyCourseTableParams params);

}
