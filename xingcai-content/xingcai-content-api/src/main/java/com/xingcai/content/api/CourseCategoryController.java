package com.xingcai.content.api;


import com.xingcai.content.model.dto.CourseCategoryTreeDto;
import com.xingcai.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseCategoryController {

    @Autowired
    CourseCategoryService categoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {


        return categoryService.queryTreeNodes("1");
    }





}
