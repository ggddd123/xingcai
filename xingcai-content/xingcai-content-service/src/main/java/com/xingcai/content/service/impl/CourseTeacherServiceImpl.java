package com.xingcai.content.service.impl;

import com.xingcai.base.exception.CustomException;
import com.xingcai.content.mapper.CourseBaseMapper;
import com.xingcai.content.mapper.CourseTeacherMapper;
import com.xingcai.content.model.po.CourseBase;
import com.xingcai.content.model.po.CourseTeacher;
import com.xingcai.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Override
    public List<CourseTeacher> getCourseTeacherByCourseId(Long courseId) {

        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectByCourseId(courseId);
        return courseTeachers;
    }

    /*
    * 添加或修改教师信息
    * */
    @Override
    public CourseTeacher saveCourseTeacher(Long companyId,CourseTeacher courseTeacher) {

        Long courseId = courseTeacher.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(!courseBase.getCompanyId().equals(companyId)){
            CustomException.cast("只能修改本机构的教师信息");
        }

        Long id = courseTeacher.getId();
        //此为添加教师信息
        if(id==null){
            CourseTeacher courseTeacher1 = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher,courseTeacher1);
            courseTeacher1.setCreateDate(LocalDateTime.now());
            courseTeacherMapper.insert(courseTeacher1);
            return courseTeacher1;
        }
        //此为修改教师信息
        int i = courseTeacherMapper.updateById(courseTeacher);
        if(i<0){
            CustomException.cast("修改失败");
        }

        return courseTeacher;
    }

    @Override
    public void deleteCourseTeacher(Long id) {
        courseTeacherMapper.deleteById(id);
    }
}
