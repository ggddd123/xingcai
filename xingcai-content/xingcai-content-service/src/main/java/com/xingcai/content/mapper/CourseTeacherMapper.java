package com.xingcai.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingcai.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 Mapper 接口
 * </p>
 *
 */
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

    List<CourseTeacher> selectByCourseId(Long courseId);
}
