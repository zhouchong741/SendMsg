package com.zc741.sendmsg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/5/7 10:01.
 */
public class HttpUtil {

    public static RequestParam getParams() {
        RequestParam params = new RequestParam();
        params.put("clientId", Constants.CLIENTID);
        params.put("clientSecret", Constants.CLIENTSECRET);
        params.put("deviceId", Constants.DEVICEID);
        String randomString = getRandomString(32);
        params.put("nonce", randomString);
        String paraTemp = HttpUtil.createParaString(HttpUtil.paraFilter(params.get()));
        String sign = MD5Utils.MD5Encode(paraTemp, "UTF-8").toUpperCase();
        params.put("sign", sign);
        return params;
    }

    // for sent
    public static String forSentParams() {
        RequestParam params = new RequestParam();
        params.put("clientId", Constants.CLIENTID);
        params.put("clientSecret", Constants.CLIENTSECRET);
        params.put("deviceId", Constants.DEVICEID);
        String randomString = getRandomString(32);
        params.put("nonce", randomString);
        String paraTemp = HttpUtil.createParaString(HttpUtil.paraFilter(params.get()));
        String sign = MD5Utils.MD5Encode(paraTemp, "UTF-8").toUpperCase();
        params.put("sign", sign);
        String sentParams = HttpUtil.createParaString(params.get());
        return sentParams;
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

    // 随机字符串
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        int number = 0;
        for (int i = 0; i < length; i++) {
            number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
