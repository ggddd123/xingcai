package com.xingcai.search.service;

import com.xingcai.base.model.PageParams;
import com.xingcai.search.dto.SearchCourseParamDto;
import com.xingcai.search.dto.SearchPageResultDto;
import com.xingcai.search.po.CourseIndex;


public interface CourseSearchService {



    SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);

 }
