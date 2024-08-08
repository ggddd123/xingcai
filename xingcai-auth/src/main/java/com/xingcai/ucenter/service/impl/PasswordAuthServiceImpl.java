package com.xingcai.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingcai.ucenter.feignclient.CheckCodeClient;
import com.xingcai.ucenter.mapper.XcUserMapper;
import com.xingcai.ucenter.model.dto.AuthParamsDto;
import com.xingcai.ucenter.model.dto.XcUserExt;
import com.xingcai.ucenter.model.po.XcUser;
import com.xingcai.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {


        //验证码校验
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if(StringUtils.isEmpty(checkcode) ||StringUtils.isEmpty(checkcodekey)){
            throw new RuntimeException("请输入验证码");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(verify==null||!verify){
            throw new RuntimeException("验证码输入错误");
        }


        //查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, authParamsDto.getUsername()));
        //如果用户不存在
        if(xcUser==null){
            throw new RuntimeException("用户不存在");
        }
        //拿到密码
        String password = xcUser.getPassword();
        String password1 = authParamsDto.getPassword();

        boolean matches = passwordEncoder.matches(password1, password);
        if(!matches){
            throw new RuntimeException("密码错误");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);


        return xcUserExt;
    }
}
