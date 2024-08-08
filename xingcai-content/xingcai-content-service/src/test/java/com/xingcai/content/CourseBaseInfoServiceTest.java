package com.xingcai.content;

import com.xingcai.base.model.PageParams;
import com.xingcai.base.model.PageResult;
import com.xingcai.content.model.dto.QueryCourseParamsDto;
import com.xingcai.content.model.po.CourseBase;
import com.xingcai.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseInfoServiceTest {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Test
    public void test1(){
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setPublishStatus("203001");

        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(2L);


        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println(courseBasePageResult);
    }

}
