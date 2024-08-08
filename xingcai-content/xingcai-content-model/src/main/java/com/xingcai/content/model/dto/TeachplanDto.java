package com.xingcai.content.model.dto;

import com.xingcai.content.model.po.Teachplan;
import com.xingcai.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;



/*
* 课程计划信息模型表
* */
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //与媒资关联的信息
    private TeachplanMedia teachplanMedia;

    //小章节list
    private List<TeachplanDto> teachPlanTreeNodes;
}
