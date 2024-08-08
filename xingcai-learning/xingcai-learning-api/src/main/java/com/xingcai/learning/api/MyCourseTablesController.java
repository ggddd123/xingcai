package com.xingcai.learning.api;

import com.xingcai.base.exception.CustomException;

import com.xingcai.base.model.PageResult;
import com.xingcai.learning.model.dto.MyCourseTableParams;
import com.xingcai.learning.model.dto.XcChooseCourseDto;
import com.xingcai.learning.model.dto.XcCourseTablesDto;
import com.xingcai.learning.model.po.XcCourseTables;
import com.xingcai.learning.service.MyCourseTablesService;
import com.xingcai.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {

    @Autowired
    MyCourseTablesService myCourseTablesService;


    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {

        //当前登录的用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            CustomException.cast("请登录");
        }
        //用户id
        String userId = user.getId();
        //添加选课
        XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(userId, courseId);
        return xcChooseCourseDto;
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        //当前登录的用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            CustomException.cast("请登录");
        }
        //用户id
        String userId = user.getId();

        XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);

        return learningStatus;

    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {

        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            CustomException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        //设置当前的登录用户
        params.setUserId(userId);

        return myCourseTablesService.mycourestabls(params);

    }

}
