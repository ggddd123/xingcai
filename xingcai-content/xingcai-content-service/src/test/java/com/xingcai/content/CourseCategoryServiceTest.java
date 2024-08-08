package com.xingcai.content;


import com.xingcai.content.model.dto.CourseCategoryTreeDto;
import com.xingcai.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseCategoryServiceTest {


    @Autowired
    CourseCategoryService categoryService;
    @Test
    public void CourseCategoryServiceTest(){

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);

    }
}
