package com.example.hxds.dr.service.impl;

import com.example.hxds.common.exception.HxdsException;
import com.example.hxds.dr.db.dao.WalletDao;
import com.example.hxds.dr.db.dao.WalletIncomeDao;
import com.example.hxds.dr.db.pojo.WalletIncomeEntity;
import com.example.hxds.dr.service.WalletIncomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
@Slf4j
public class WalletIncomeServiceImpl {
//public class WalletIncomeServiceImpl implements WalletIncomeService {
    @Resource
    private WalletDao walletDao;

    @Resource
    private WalletIncomeDao walletIncomeDao;

/*    @Override
    @Transactional
    public int transfer(WalletIncomeEntity entity) {
        int rows = walletIncomeDao.insert(entity);
        if(rows!=1){
            throw new HxdsException("添加转账记录失败");
        }

        HashMap param=new HashMap(){{
            put("driverId",entity.getDriverId());
            put("amount", entity.getAmount());
        }};
        rows = walletDao.updateWalletBalance(param);
        if(rows!=1){
            throw new HxdsException("更新钱包余额失败");
        }
        return rows;
    }*/
}
