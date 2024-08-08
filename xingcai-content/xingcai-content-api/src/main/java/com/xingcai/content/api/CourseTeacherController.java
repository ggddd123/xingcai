package com.xingcai.content.api;


import com.xingcai.content.model.po.CourseTeacher;
import com.xingcai.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * 课程计划管理相关接口
 * */
@Api(value = "课程老师编辑接口",tags = "课程老师编辑接口")
@RestController
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("根据课程ID查询教师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacherByCourseId(@PathVariable Long courseId){
        List<CourseTeacher> courseTeachers =courseTeacherService.getCourseTeacherByCourseId(courseId);
        return courseTeachers;
    }

    @ApiOperation("添加和修改教师信息")
    @PostMapping ("/courseTeacher")
    public CourseTeacher saveCourseTeacher (@RequestBody @Validated CourseTeacher courseTeacher){
        Long companyId = 1232141425L;
        CourseTeacher courseTeacher1 =courseTeacherService.saveCourseTeacher(companyId,courseTeacher);
        return courseTeacher1;
    }

    @ApiOperation("删除教师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable Long courseId,@PathVariable Long id){
         courseTeacherService.deleteCourseTeacher(id);

    }


}
