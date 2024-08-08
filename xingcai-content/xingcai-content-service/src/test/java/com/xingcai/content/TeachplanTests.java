package com.xingcai.content;


import com.xingcai.content.mapper.TeachplanMapper;
import com.xingcai.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TeachplanTests {


    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public  void test() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }
}
