package com.zc741.sendmsg.utils;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/3/28 11:32.
 */

public class HttpUrls {

    public static final String SERVER_HOST = "http://t.immi-api.immistudy.com/";

    public static final String GET_INFO = "";

    public static final String YIMI = "yimi";

    /**
     * 获取未发送的短信 GET
     */
    public static final String  UNSENT = "rest/sms/unsent";

    /**
     * 修改短信的状态为已发送 POST
     */
    public static final String SENT = "rest/sms/sent";

    public static String makeUrl(String url, String tag) {
        String result = "";
        switch (tag) {
            case YIMI:
                result = SERVER_HOST + url;
                break;
        }
        return result;
    }
}
