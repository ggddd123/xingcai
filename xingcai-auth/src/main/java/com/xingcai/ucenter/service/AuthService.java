package com.xingcai.ucenter.service;

import com.xingcai.ucenter.model.dto.AuthParamsDto;
import com.xingcai.ucenter.model.dto.XcUserExt;

public interface AuthService {
    XcUserExt execute(AuthParamsDto authParamsDto);
}
