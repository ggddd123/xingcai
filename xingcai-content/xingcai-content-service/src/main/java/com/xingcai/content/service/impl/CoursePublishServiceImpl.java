package com.xingcai.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xingcai.base.exception.CommonError;
import com.xingcai.base.exception.CustomException;
import com.xingcai.content.config.MultipartSupportConfig;
import com.xingcai.content.feignclient.MediaServiceClient;
import com.xingcai.content.mapper.CourseBaseMapper;
import com.xingcai.content.mapper.CourseMarketMapper;
import com.xingcai.content.mapper.CoursePublishMapper;
import com.xingcai.content.mapper.CoursePublishPreMapper;
import com.xingcai.content.model.dto.CourseBaseInfoDto;
import com.xingcai.content.model.dto.CoursePreviewDto;
import com.xingcai.content.model.dto.TeachplanDto;
import com.xingcai.content.model.po.CourseBase;
import com.xingcai.content.model.po.CourseMarket;
import com.xingcai.content.model.po.CoursePublish;
import com.xingcai.content.model.po.CoursePublishPre;
import com.xingcai.content.service.CourseBaseInfoService;
import com.xingcai.content.service.CoursePublishService;
import com.xingcai.content.service.TeachplanService;
import com.xingcai.messagesdk.model.po.MqMessage;
import com.xingcai.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Autowired
    RedisTemplate redisTemplate;




    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfoDto(courseId);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setTeachplans(teachplanTree);

        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {

        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfoDto(courseId);
        if(courseBaseInfoDto==null){
            CustomException.cast("课程找不到");
        }

        String auditStatus = courseBaseInfoDto.getAuditStatus();
        if(auditStatus.equals("202003")){
            CustomException.cast("课程已提交");
        }
        //课程图片是否填写
        if(StringUtils.isEmpty(courseBaseInfoDto.getPic())){
            CustomException.cast("提交失败，请上传课程图片");
        }
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if(teachplanTree.size()<=0){
            CustomException.cast("提交失败，还没有添加课程计划");
        }


        //查询课程基本信息，营销信息，计划信息等插入到课程预发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfoDto,coursePublishPre);
        coursePublishPre.setCompanyId(companyId);

        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String s = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(s);

        //计划信息
        String s1 = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(s1);

        //状态已提交
        coursePublishPre.setStatus("202003");
        //提交时间
        coursePublishPre.setAuditDate(LocalDateTime.now());

        //查询预发布表，如有记录则更新
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre1==null){
            coursePublishPreMapper.insert(coursePublishPre);
        }else {
            //插入
            coursePublishPreMapper.updateById(coursePublishPre);
        }


        //更新课程基本信息表的审核状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("203003");//审核状态为已提交
        courseBaseMapper.updateById(courseBase);
    }

    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {

        //查询课程预发布表向发布表写数据
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        String status = coursePublishPre.getStatus();
        if(!status.equals("202004")){
            CustomException.cast("课程没有审核通过，不许发布");
        }

        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        //先查询课程发布表
        CoursePublish coursePublish1 = coursePublishMapper.selectById(courseId);
        if(coursePublish1==null){
            coursePublishMapper.insert(coursePublish);
        }else {
            coursePublishMapper.updateById(coursePublish);
        }



        //向消息表写数据
        saveCoursePubishMessage(courseId);


        //删除
        coursePublishPreMapper.deleteById(coursePublishPre);

    }

    @Override
    public File generateCourseHtml(Long courseId) {
        File file = null;
        try {
            Configuration configuration = new Configuration(Configuration.getVersion());
            //拿到classpath路径
            String path = this.getClass().getResource("/").getPath();
            //指定模版的目录
            configuration.setDirectoryForTemplateLoading(new File(path+"/templates"));
            configuration.setDefaultEncoding("utf-8");
            //得到模版
            Template template = configuration.getTemplate("course_template.ftl");

            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("model",coursePreviewInfo);


            String s = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

            InputStream stream = IOUtils.toInputStream(s, "utf-8");
            file=File.createTempFile("coursepublish",".html");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            IOUtils.copy(stream,fileOutputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {


        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);


        mediaServiceClient.uploadFile(multipartFile,"course/"+courseId+".html");
    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish ;

    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {

        //查询缓存
        Object  jsonObj = redisTemplate.opsForValue().get("course:" + courseId);
        if(jsonObj!=null){
            String jsonString = jsonObj.toString();
            System.out.println("=================从缓存查=================");
            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
            return coursePublish;
        } else {
            System.out.println("从数据库查询...");
            //从数据库查询
            CoursePublish coursePublish = getCoursePublish(courseId);
            if(coursePublish!=null){
                redisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish));
            }
            return coursePublish;
        }


}


    /*
    * 保存消息记录表
    * */
    private void saveCoursePubishMessage(Long courseId) {
        MqMessage coursePublish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(coursePublish==null){
            CustomException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
