package com.xingcai.learning.service.impl;

import com.xingcai.base.model.RestResponse;
import com.xingcai.content.model.po.CoursePublish;
import com.xingcai.learning.feignclient.ContentServiceClient;
import com.xingcai.learning.feignclient.MediaServiceClient;
import com.xingcai.learning.model.dto.XcCourseTablesDto;
import com.xingcai.learning.service.LearningService;
import com.xingcai.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {


        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if(coursepublish==null){
            return RestResponse.validfail("课程为空");
        }



        if(StringUtils.isNotEmpty(userId)){
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if(learnStatus.equals("702002")){
                return RestResponse.validfail("无法学习，没用选课或没用支付");
            } else if (learnStatus.equals("702003")) {
                return RestResponse.validfail("已过期");
            }else {
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;

            }
        }

        //如果用户没用登录
        String charge = coursepublish.getCharge();
        if("201000".equals(charge)){
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;

        }
        return RestResponse.validfail("该课程没用选课");
    }
}
