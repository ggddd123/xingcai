package com.xingcai.content.service;

import com.xingcai.content.model.dto.BindTeachplanMediaDto;
import com.xingcai.content.model.dto.SaveTeachplanDto;
import com.xingcai.content.model.dto.TeachplanDto;
import com.xingcai.content.model.po.TeachplanMedia;

import java.util.List;



public interface TeachplanService {

    public List<TeachplanDto> findTeachplanTree(Long couresId);


    /*
    * 新增，修改课程信息
    * */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);

    /*
    * 删除课程计划
    * */
    void deleteTeachplan(Long id);

    /*
    * 排序下移
    * */
    void moveDown(Long id);

    /*
     * 排序上移
     * */
    void moveUp(Long id);

    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    void unBindMedia(Long teachPlanId, String mediaId);
}
