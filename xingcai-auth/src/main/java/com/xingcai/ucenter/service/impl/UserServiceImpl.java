package com.xingcai.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xingcai.ucenter.mapper.XcMenuMapper;
import com.xingcai.ucenter.mapper.XcUserMapper;
import com.xingcai.ucenter.model.dto.AuthParamsDto;
import com.xingcai.ucenter.model.dto.XcUserExt;
import com.xingcai.ucenter.model.po.XcMenu;
import com.xingcai.ucenter.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService {


    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Autowired
    ApplicationContext applicationContext;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        //将传入的JSON转出Dto对象
        AuthParamsDto authParamsDto=null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        }catch (Exception e){
            throw new RuntimeException("请求认证的参数不符合要求");
        }

        String authType = authParamsDto.getAuthType();
        //根据类型从Spring容器选出对应的bean
        String beanType=authType+"_authservice";
        AuthService authService = applicationContext.getBean(beanType, AuthService.class);

        XcUserExt execute = authService.execute(authParamsDto);
        UserDetails userPrincipal = getUserPrincipal(execute);


        return userPrincipal;
    }

    /*
    * 查询用户权限
    * */
    public UserDetails getUserPrincipal(XcUserExt user){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};
        //根据用户ID查询权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        if(xcMenus.size()>0){
            List<String> permissions =new ArrayList<>();
            for (XcMenu xcMenu : xcMenus) {
                permissions.add(xcMenu.getCode());
            }
            //将permissions转成数组
            authorities = permissions.toArray(new String[0]);
        }

        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password ).authorities(authorities).build();
        return userDetails;
    }

}
