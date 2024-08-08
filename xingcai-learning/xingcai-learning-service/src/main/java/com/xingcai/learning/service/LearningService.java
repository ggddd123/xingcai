package com.xingcai.learning.service;

import com.xingcai.base.model.RestResponse;

public interface LearningService {

    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
