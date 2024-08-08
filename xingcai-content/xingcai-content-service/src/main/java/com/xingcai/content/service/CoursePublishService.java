package com.xingcai.content.service;

import com.xingcai.content.model.dto.CoursePreviewDto;
import com.xingcai.content.model.po.CoursePublish;

import java.io.File;


/*
* 课程发布相关信息
* */
public interface CoursePublishService {

    /*
    * 获取课程预览信息
    * */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);


    //提交审核
    public void commitAudit(Long companyId,Long courseId);

    //课程发布
    public void publish(Long companyId,Long courseId);

    //静态化网页
    public File generateCourseHtml(Long courseId);

    //上传静态化网页
    public void  uploadCourseHtml(Long courseId,File file);

    CoursePublish getCoursePublish(Long courseId);

    public CoursePublish getCoursePublishCache(Long courseId);
}
