package com.zc741.sendmsg.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * 签名工具类
 *
 * @author Huangwy
 * @TIME 2016/6/21 11:48
 */
public class SignUtils {

    private static final String key = "7VxwGfGnYFYgOUf0Qrq6SuZPF9FFB2Z6";

    @SuppressWarnings("rawtypes")
    public static String createSign(String characterEncoding, TreeMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);
//        System.out.println("key：" + key);
//        System.out.println("字符串拼接后是：" + sb.toString());
        String sign = MD5Utils.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
//        System.out.println("sign：" + sign);
        return sign;
    }

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
