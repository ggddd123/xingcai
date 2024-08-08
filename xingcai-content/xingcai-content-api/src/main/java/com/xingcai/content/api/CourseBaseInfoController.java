package com.xingcai.content.api;

import com.xingcai.base.model.PageParams;
import com.xingcai.base.model.PageResult;
import com.xingcai.content.model.dto.AddCourseDto;
import com.xingcai.content.model.dto.CourseBaseInfoDto;
import com.xingcai.content.model.dto.EditCourseDto;
import com.xingcai.content.model.dto.QueryCourseParamsDto;
import com.xingcai.content.model.po.CourseBase;
import com.xingcai.content.service.CourseBaseInfoService;
import com.xingcai.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(value = "课程信息管理接口",tags = "课程信息管理接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程分页查询接口")
    @PostMapping("/course/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required=false) QueryCourseParamsDto queryCourseParamsDto) {

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        return courseBasePageResult;

    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){

        Long id = 1232141425L;
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(id, addCourseDto);

        return courseBase;
    }

    @ApiOperation("根据课程ID查询")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){

//       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);


        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfoDto(courseId);
        return courseBaseInfoDto;

    }


    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }


    @ApiOperation("删除课程基础信息")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourse(@PathVariable  Long courseId){

        courseBaseInfoService.deleteCourse(courseId);
    }
}
