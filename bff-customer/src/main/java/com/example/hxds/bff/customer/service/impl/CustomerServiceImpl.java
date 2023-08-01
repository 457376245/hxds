package com.example.hxds.bff.customer.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hxds.bff.customer.controller.form.*;
import com.example.hxds.bff.customer.feign.CstServiceApi;
import com.example.hxds.bff.customer.service.CustomerService;
import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.common.util.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CstServiceApi cstServiceApi;

//
//    @Override
//    @Transactional
//    
//    public long registerNewCustomer(RegisterNewCustomerForm form) {
//        R r = cstServiceApi.registerNewCustomer(form);
//        long userId = Convert.toLong(r.get("userId"));
//        return userId;
//    }
//
//    @Override
//    public Long login(LoginForm form) {
//        R r = cstServiceApi.login(form);
//        String userId = MapUtil.getStr(r, "userId");
//        if (!StrUtil.isBlank(userId)) {
//            return Convert.toLong(userId);
//        }
//        return null;
//    }


}
