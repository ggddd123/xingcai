package com.xingcai.checkcode.service.impl;

import com.xingcai.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component("UUIDKeyGenerator")
public class UUIDKeyGenerator implements CheckCodeService.KeyGenerator {
    @Override
    public String generate(String prefix) {
        String uuid = UUID.randomUUID().toString();
        return prefix + uuid.replaceAll("-", "");
    }
}
