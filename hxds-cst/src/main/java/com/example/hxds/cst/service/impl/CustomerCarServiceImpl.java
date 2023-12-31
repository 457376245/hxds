package com.example.hxds.cst.service.impl;

import com.example.hxds.cst.db.dao.CustomerCarDao;
import com.example.hxds.cst.db.pojo.CustomerCarEntity;
import com.example.hxds.cst.service.CustomerCarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CustomerCarServiceImpl implements CustomerCarService {
    @Resource
    private CustomerCarDao customerCarDao;

    @Override
    @Transactional
     
    public void insertCustomerCar(CustomerCarEntity entity) {
        customerCarDao.insert(entity);
    }

    @Override
    public ArrayList<HashMap> searchCustomerCarList(long customerId) {
        ArrayList<HashMap> list = customerCarDao.searchCustomerCarList(customerId);
        return list;
    }

    @Override
    @Transactional
     
    public int deleteCustomerCarById(long id) {
        int rows = customerCarDao.deleteCustomerCarById(id);
        return rows;
    }
}
