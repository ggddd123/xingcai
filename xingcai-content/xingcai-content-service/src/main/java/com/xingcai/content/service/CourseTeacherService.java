package com.xingcai.content.service;

import com.xingcai.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    /*
    * 根据课程ID查询教师
    * */
    List<CourseTeacher> getCourseTeacherByCourseId(Long courseId);

    /*
    * 添加和修改教师信息
    * */
    CourseTeacher saveCourseTeacher(Long companyId,CourseTeacher courseTeacher);


    /*
    * 删除教师信息
    * */
    void deleteCourseTeacher(Long id);
}
