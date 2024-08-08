package com.xingcai.checkcode.service;

import com.xingcai.checkcode.model.CheckCodeParamsDto;
import com.xingcai.checkcode.model.CheckCodeResultDto;


public interface CheckCodeService {



     CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);


    public boolean verify(String key, String code);


    public interface CheckCodeGenerator{
        /**
         * 验证码生成
         * @return 验证码
         */
        String generate(int length);
        

    }


    public interface KeyGenerator{

        /**
         * key生成
         * @return 验证码
         */
        String generate(String prefix);
    }



    public interface CheckCodeStore{


        void set(String key, String value, Integer expire);

        String get(String key);

        void remove(String key);
    }
}
