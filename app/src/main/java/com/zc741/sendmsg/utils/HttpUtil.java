package com.zc741.sendmsg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/5/7 10:01.
 */
public class HttpUtil {

    public static RequestParam getParams( RequestParam params){
        params.put("appKey", Constants.APPKEY);
        params.put("timeStamp", String.valueOf(TimeUtil.timeStamp() / 1000));
        params.put("deviceId", "");
        String randomString = SignUtils.getRandomString(32);
        params.put("nonceStr", randomString);
        String paraTemp = HttpUtil.createParaString(HttpUtil.paraFilter(params.get()));
        String sign = MD5Utils.MD5Encode(paraTemp, "UTF-8").toUpperCase();
        params.put("sign", sign);

        return params;
    }



    // 排序
    public static String createParaString(Map<String, String> params) {
        java.util.List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    // 除空值
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
}
