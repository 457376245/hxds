package com.example.hxds.common.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.hxds.common.exception.HxdsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component("MicroAppUtil")
public class MicroAppUtil {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    /**
     * 通过临时短信验证码code  获得对应手机号用户的OpenId
     * @param code
     * @return
     */
    public String getOpenId(String code) {
/*        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;*/
        return code;
    }

    public String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        HashMap map = new HashMap() {{
            put("grant_type", "client_credential");
            put("appid", appId);
            put("secret", appSecret);
        }};
        String response = HttpUtil.get(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("access_token")) {
            String accessToken = json.getStr("access_token");
            return accessToken;
        } else {
            throw new HxdsException(json.getStr("errmsg"));
        }
    }

    public String getTel(String photeCode) {
//        String accessToken = getAccessToken();
//        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
//        JSONObject param = new JSONObject();
//        param.set("code", photeCode);
//        HttpRequest post = HttpUtil.createPost(url);
//        post.body(param.toString(), "application/json");
//        HttpResponse response = post.execute();
//        JSONObject json = JSONUtil.parseObj(response.body());
//        if (json.containsKey("phone_info")) {
////            String tel = json.getJSONObject("phone_info").getStr("phoneNumber");
//            String tel = json.getJSONObject("phone_info").getStr("purePhoneNumber");
//            return tel;
//        } else {
//            throw new HxdsException(json.getStr("errmsg"));
//        }
//    }
        return photeCode;
    }
}
