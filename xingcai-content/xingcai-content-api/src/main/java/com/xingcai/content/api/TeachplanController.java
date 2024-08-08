package com.xingcai.content.api;


import com.xingcai.content.model.dto.BindTeachplanMediaDto;
import com.xingcai.content.model.dto.SaveTeachplanDto;
import com.xingcai.content.model.dto.TeachplanDto;
import com.xingcai.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 课程计划管理相关接口
* */
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {


    @Autowired
    TeachplanService teachplanService;


    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        return teachplanTree;
    }


    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
        teachplanService.saveTeachplan(saveTeachplanDto);

    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id){
        teachplanService.deleteTeachplan(id);

    }

    @ApiOperation("课程计划排序：下移")
    @PostMapping ("/teachplan/movedown/{id}")
    public void movedown(@PathVariable Long id){
        teachplanService.moveDown(id);

    }

    @ApiOperation("课程计划排序：上移")
    @PostMapping ("/teachplan/moveup/{id}")
    public void moveup(@PathVariable Long id){
        teachplanService.moveUp(id);

    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){

        teachplanService.associationMedia(bindTeachplanMediaDto);


    }


    @ApiOperation(value = "课程计划和媒资信息解绑")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void unBindMedia(@PathVariable Long teachPlanId,@PathVariable String mediaId){

        teachplanService.unBindMedia(teachPlanId,mediaId);


    }


}
