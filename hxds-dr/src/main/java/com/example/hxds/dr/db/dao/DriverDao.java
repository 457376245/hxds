package com.example.hxds.dr.db.dao;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DriverDao {

    public int registerNewDriver(Map param);

    public long hasDriver(Map param);

    public String searchDriverId(String openId);

}




