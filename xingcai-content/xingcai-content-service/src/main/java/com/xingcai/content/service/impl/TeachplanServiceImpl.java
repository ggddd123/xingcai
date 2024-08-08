package com.xingcai.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingcai.base.exception.CustomException;
import com.xingcai.content.mapper.TeachplanMapper;
import com.xingcai.content.mapper.TeachplanMediaMapper;
import com.xingcai.content.model.dto.BindTeachplanMediaDto;
import com.xingcai.content.model.dto.SaveTeachplanDto;
import com.xingcai.content.model.dto.TeachplanDto;
import com.xingcai.content.model.po.Teachplan;
import com.xingcai.content.model.po.TeachplanMedia;
import com.xingcai.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {


    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    //查询课程计划树
    @Override
    public List<TeachplanDto> findTeachplanTree(Long couresId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(couresId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        Long id = teachplanDto.getId();
        if( id == null){
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto,teachplan);
            //确定排序的字段
            Long parentid = teachplanDto.getParentid();
            Long courseId = teachplan.getCourseId();
            Integer teachplanOrder = getTeachplanOrder(parentid, courseId);
            teachplan.setOrderby(teachplanOrder);

            teachplanMapper.insert(teachplan);
        }else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    /*
    * 删除课程计划
    * */
    @Override
    public void deleteTeachplan(Long id) {
        //判断若为第一级别的章则要求章下边没有小节方可删
        Teachplan teachplan = teachplanMapper.selectById(id);
        List<Teachplan> teachplans = teachplanMapper.selectByParentId(id);

        if(teachplan.getParentid()==0&&!(teachplans.size()==0)){
            CustomException.cast("课程计划信息还有子级信息，无法操作");
        }
        teachplanMapper.deleteById(id);
        List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectByTeachplanId(id);
        //如果小节里有视频则删除
        if (teachplanMedia.size()!=0){
            for (TeachplanMedia media : teachplanMedia) {
                teachplanMediaMapper.deleteById(media.getId());
            }
        }

    }

    /*
    * 排序下移
    * */
    @Override
    public void moveDown(Long id) {
        //找到和排序目标同级的其他课程计划
        List<Teachplan> teachplans = getTeachplansByid(id);
        Teachplan teachplan = teachplanMapper.selectById(id);

        //排序对象在集合中的索引
        int i = teachplans.indexOf(teachplan);

        //判断，如果排序目标是是最后一个
        if(i==(teachplans.size()-1)){
            //将排序目标与其后一位的order进行交换
            CustomException.cast("无法进行下移");
        }

        //如果排序目标与其后一位的order相同
        if(teachplans.get(i).getOrderby()==teachplans.get(i+1).getOrderby()){
            teachplans.get(i).setOrderby(teachplans.get(i).getOrderby()+1);
            teachplanMapper.updateById(teachplans.get(i));
        }else {
            //将排序目标与其后一位的order进行交换
            Integer orderby = teachplans.get(i).getOrderby();
            teachplans.get(i).setOrderby(teachplans.get(i+1).getOrderby());
            teachplans.get(i+1).setOrderby(orderby);
            //将更改后的数据写入数据库
            teachplanMapper.updateById(teachplans.get(i));
            teachplanMapper.updateById(teachplans.get(i+1));
        }


    }

    /*
    * 排序上移
    * */
    @Override
    public void moveUp(Long id) {
        //找到和排序目标同级的其他课程计划
        List<Teachplan> teachplans = getTeachplansByid(id);
        Teachplan teachplan = teachplanMapper.selectById(id);

        //排序对象在集合中的索引
        int i = teachplans.indexOf(teachplan);

        //判断，如果排序目标是第一个
        if(i==0){
            //将排序目标与其后一位的order进行交换
            CustomException.cast("无法进行上移");
        }

        //如果排序目标与其前一位的order相同
        if(teachplans.get(i).getOrderby()==teachplans.get(i-1).getOrderby()){
            teachplans.get(i).setOrderby(teachplans.get(i).getOrderby()-1);
            teachplanMapper.updateById(teachplans.get(i));
        }else {
            //将排序目标与其前一位的order进行交换
            Integer orderby = teachplans.get(i).getOrderby();
            teachplans.get(i).setOrderby(teachplans.get(i-1).getOrderby());
            teachplans.get(i-1).setOrderby(orderby);
            //将更改后的数据写入数据库
            teachplanMapper.updateById(teachplans.get(i));
            teachplanMapper.updateById(teachplans.get(i-1));
        }
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {

        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            CustomException.cast("课程计划不存在");
        }
        //删除原有记录
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));


        //添加新记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMediaMapper.insert(teachplanMedia);

        return null;
    }

    @Override
    public void unBindMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(TeachplanMedia::getTeachplanId,teachPlanId).eq(TeachplanMedia::getMediaId,mediaId);
        teachplanMediaMapper.delete(QueryWrapper);
    }

    //找到和排序目标同级的其他课程计划
    private List<Teachplan> getTeachplansByid(Long id){
        //找到和排序目标同级的其他课程计划
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long parentid = teachplan.getParentid();
        Long courseId = teachplan.getCourseId();
        LambdaQueryWrapper<Teachplan> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.eq(Teachplan::getCourseId,courseId).eq(Teachplan::getParentid,parentid).orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(LambdaQueryWrapper);
        return teachplans;
    }

    /*
    * 获取排序的字段
    * */
    private Integer getTeachplanOrder(Long parentid,Long courseId){

        LambdaQueryWrapper<Teachplan> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.eq(Teachplan::getCourseId,courseId).eq(Teachplan::getParentid,parentid).orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(LambdaQueryWrapper);
        if(teachplans.size()==0){
            return Integer.valueOf(1);
        }
        Teachplan teachplan1 = teachplans.get(teachplans.size() - 1);
        Integer orderby1 = teachplan1.getOrderby();
        return ++orderby1;

    }
}
