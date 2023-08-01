package com.example.hxds.mps.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.example.hxds.mps.service.DriverLocationService;
import com.example.hxds.mps.util.CoordinateTransform;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DriverLocationServiceImpl implements DriverLocationService {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void updateLocationCache(Map param) {
        long driverId = MapUtil.getLong(param, "driverId");
        String latitude = MapUtil.getStr(param, "latitude");
        String longitude = MapUtil.getStr(param, "longitude");
        //接单范围
        int rangeDistance = MapUtil.getInt(param, "rangeDistance");
        //订单里程范围
        int orderDistance = MapUtil.getInt(param, "orderDistance");

        Point point = new Point(Convert.toDouble(longitude), Convert.toDouble(latitude));
        redisTemplate.opsForGeo().add("driver_location", point, driverId + "");

        //定向接单地址的经度
        String orientateLongitude = null;
        if (param.get("orientateLongitude") != null) {
            orientateLongitude = MapUtil.getStr(param, "orientateLongitude");
        }
        //定向接单地址的纬度
        String orientateLatitude = null;
        if (param.get("orientateLatitude") != null) {
            orientateLatitude = MapUtil.getStr(param, "orientateLatitude");
        }

        //定向接单经纬度的字符串
        String orientation = "none";
        if (orientateLongitude != null && orientateLatitude != null) {
            orientation = orientateLatitude + "," + orientateLongitude;
        }

        String temp = rangeDistance + "#" + orderDistance + "#" + orientation;
        redisTemplate.opsForValue().set("driver_online#" + driverId, temp, 60, TimeUnit.SECONDS);

    }

    @Override
    public void removeLocationCache(long driverId) {
        redisTemplate.opsForGeo().remove("driver_location", driverId + "");
        redisTemplate.delete("driver_online#" + driverId);
    }

    @Override
    public ArrayList searchBefittingDriverAboutOrder(double startPlaceLatitude,
                                                     double startPlaceLongitude,
                                                     double endPlaceLatitude,
                                                     double endPlaceLongitude,
                                                     double mileage) {
        //客户下单点
        Point point = new Point(startPlaceLongitude, startPlaceLatitude);
        //距离单位
        Metric metric = RedisGeoCommands.DistanceUnit.KILOMETERS;
        //客户下单点5公里
        Distance distance = new Distance(5, metric);
        //客户下单点5公里圆心
        Circle circle = new Circle(point, distance);

        //定义结果返回格式
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance().includeCoordinates().sortAscending();
        
        //根据circle获取driver_location中符合条件的地理位置集合
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius = redisTemplate.opsForGeo().radius("driver_location", circle, args);

        ArrayList list = new ArrayList();
        if (radius != null) {
            //迭代器遍历返回结果
            Iterator<GeoResult<RedisGeoCommands.GeoLocation<String>>> iterator = radius.iterator();
            while (iterator.hasNext()) {
                GeoResult<RedisGeoCommands.GeoLocation<String>> result = iterator.next();
                RedisGeoCommands.GeoLocation<String> content = result.getContent();
                //获取司机id
                String driverId = content.getName();
                double dist = result.getDistance().getValue();
                //查询该司机是否有定向接单
                if (!redisTemplate.hasKey("driver_online#" + driverId)) {
                    continue;
                }
                Object obj = redisTemplate.opsForValue().get("driver_online#" + driverId);
                if (obj == null) {
                    continue;
                }
                String value = obj.toString();
                //定向接单格式为：rangeDistance#orderDistance#orientation  接单范围#订单里程范围#定向接单经纬度
                String[] temp = value.split("#");
                int rangeDistance = Integer.parseInt(temp[0]);
                int orderDistance = Integer.parseInt(temp[1]);
                String orientation = temp[2];

                //判断接单范围是否覆盖订单
                boolean bool_1 = (dist <= rangeDistance);
                boolean bool_2 = false;
                //判断订单里程是否小于司机接单里程
                if (orderDistance == 0) {
                    bool_2 = true;
                } else if (orderDistance == 5 && mileage > 0 && mileage <= 5) {
                    bool_2 = true;
                } else if (orderDistance == 10 && mileage > 5 && mileage <= 10) {
                    bool_2 = true;
                } else if (orderDistance == 15 && mileage > 10 && mileage <= 15) {
                    bool_2 = true;
                } else if (orderDistance == 30 && mileage > 15 && mileage <= 30) {
                    bool_2 = true;
                }

                //判断订单目的地是否在司机定向接单地点3公里以内
                boolean bool_3 = false;
                if (!orientation.equals("none")) {
                    double orientationLatitude = Double.parseDouble(orientation.split(",")[0]);
                    double orientationLongitude = Double.parseDouble(orientation.split(",")[1]);
                    //把定向点的火星坐标转换成GPS坐标
                    double[] location = CoordinateTransform.transformGCJ02ToWGS84(orientationLongitude, orientationLatitude);
                    GlobalCoordinates point_1 = new GlobalCoordinates(location[1], location[0]);
                    //把订单终点的火星坐标转换成GPS坐标
                    location = CoordinateTransform.transformGCJ02ToWGS84(endPlaceLongitude, endPlaceLatitude);
                    GlobalCoordinates point_2 = new GlobalCoordinates(location[1], location[0]);
                    //这里不需要Redis的GEO计算，直接用封装函数计算两个GPS坐标之间的距离
                    GeodeticCurve geoCurve = new GeodeticCalculator()
                            .calculateGeodeticCurve(Ellipsoid.WGS84, point_1, point_2);
                    if (geoCurve.getEllipsoidalDistance() <= 3000) {
                        bool_3 = true;
                    }
                } else {
                    bool_3 = true;
                }
                //三者同时满足时返回司机id
                if (bool_1 && bool_2 && bool_3) {
                    HashMap map = new HashMap() {{
                        put("driverId", driverId);
                        put("distance", dist);
                    }};
                    list.add(map);
                }
            }
        }
        return list;
    }

    @Override
    public void updateOrderLocationCache(Map param) {
        long orderId = MapUtil.getLong(param, "orderId");
        String latitude = MapUtil.getStr(param, "latitude");
        String longitude = MapUtil.getStr(param, "longitude");
        String location=latitude+"#"+longitude;
        redisTemplate.opsForValue().set("order_location#"+orderId,location,10,TimeUnit.MINUTES);
    }

    @Override
    public HashMap searchOrderLocationCache(long orderId) {
        Object obj = redisTemplate.opsForValue().get("order_location#" + orderId);
        if(obj!=null){
            String[] temp = obj.toString().split("#");
            String latitude = temp[0];
            String longitude = temp[1];
            HashMap map=new HashMap(){{
                put("latitude",latitude);
                put("longitude",longitude);
            }};
            return map;
        }
        return null;
    }
}
