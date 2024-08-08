package com.xingcai.ucenter.service;

import com.xingcai.ucenter.model.po.XcUser;

public interface WxAuthService {

    public XcUser wxAuth(String code);
}
