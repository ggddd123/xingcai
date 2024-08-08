package com.xingcai.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingcai.base.exception.CustomException;
import com.xingcai.base.model.PageParams;
import com.xingcai.base.model.PageResult;
import com.xingcai.content.mapper.*;
import com.xingcai.content.model.dto.AddCourseDto;
import com.xingcai.content.model.dto.CourseBaseInfoDto;
import com.xingcai.content.model.dto.EditCourseDto;
import com.xingcai.content.model.dto.QueryCourseParamsDto;
import com.xingcai.content.model.po.*;
import com.xingcai.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;


    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {


        //拼接查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper=new LambdaQueryWrapper<>();
        //根据名称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,
                queryCourseParamsDto.getCourseName());
        //根据审核状态查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus());
        //根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus());



        //创建page分页查询对象
        Page<CourseBase> page=new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //开始进行分页查询
        Page<CourseBase> page1 = courseBaseMapper.selectPage(page, queryWrapper);

        List<CourseBase> records = page1.getRecords();
        long total = page1.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());

        return courseBasePageResult;

    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {

        //向课程基本信息表写入数据
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(addCourseDto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());

        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }

        //向课程营销表写入数据
        CourseMarket courseMarket = new CourseMarket();
        //课程的ID
        Long id = courseBaseNew.getId();
        courseMarket.setId(id);

        //将页面输入信息拷贝到courseMarket
        BeanUtils.copyProperties(addCourseDto,courseMarket);

        //保存营销信息
        saveCourseMarket(courseMarket);


        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfoDto(id);



        return courseBaseInfoDto;
    }



    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfoDto(Long id){
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if(courseBase==null){
            return null;
        }

        CourseMarket courseMarket = courseMarketMapper.selectById(id);

        //组装在一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);

        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());


        return courseBaseInfoDto;

    }


    /*
    * 修改课程
    * */
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        Long id = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(id);
        //修改营销信息表
        CourseMarket courseMarket = courseMarketMapper.selectById(id);

        if(courseBase==null){
            CustomException.cast("课程不存在");
        }

        //数据合法性校验
        //根据具体业务逻辑校验
        //本机构只能修改本机构的课程
        if(!companyId.equals(courseBase.getCompanyId())){
            CustomException.cast("本机构只能修改本机构的课程");
        }

        BeanUtils.copyProperties(editCourseDto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        int i = courseBaseMapper.updateById(courseBase);
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        int i1 = courseMarketMapper.updateById(courseMarket);

        if(i<=0||i1<=0){
            CustomException.cast("修改课程失败");
        }


        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfoDto(id);

        return courseBaseInfoDto;
    }


    /*
    * 删除课程
    * */
    @Override
    public void deleteCourse(Long courseId) {

        courseBaseMapper.deleteById(courseId);
        courseMarketMapper.deleteById(courseId);
        //删除课程计划
        LambdaQueryWrapper<Teachplan> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Teachplan::getCourseId,courseId);
        teachplanMapper.delete(lambdaQueryWrapper1);
        //删除课程媒资
        LambdaQueryWrapper<TeachplanMedia> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(TeachplanMedia::getCourseId,courseId);
        teachplanMediaMapper.delete(lambdaQueryWrapper2);
        //删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> lambdaQueryWrapper3 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper3.eq(CourseTeacher::getCourseId,courseId);
        courseTeacherMapper.delete(lambdaQueryWrapper3);


    }

    //单独写一个方法保存营销信息，存在则更新，不存在则添加
    private void saveCourseMarket(CourseMarket courseMarket){
        Long id = courseMarket.getId();
        CourseMarket selectById = courseMarketMapper.selectById(id);
        if(selectById==null){
            courseMarketMapper.insert(courseMarket);
        }else {
            //存在、更新
            BeanUtils.copyProperties(courseMarket,selectById);
            selectById.setId(courseMarket.getId());
            courseMarketMapper.updateById(selectById);
        }

    }
}
