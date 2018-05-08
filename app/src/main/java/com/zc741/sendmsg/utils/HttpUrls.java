package com.zc741.sendmsg.utils;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/3/28 11:32.
 */

public class HttpUrls {

    /**
     * 测试环境
     */
    public static final String TEST_HOST = "http://t.immi-api.immistudy.com/";

    /**
     * 正式环境
     */
    public static final String SERVER_HOST = "http://immi-mall.immistudy.com/";

    public static final String YIMI_TEST = "yimi_test";
    public static final String YIMI_SERVER = "yimi_server";

    /**
     * 获取未发送的短信 GET
     */
    public static final String UNSENT = "rest/sms/unsent";

    /**
     * 修改短信的状态为已发送 POST
     */
    public static final String SENT = "rest/sms/sent";

    public static String makeUrl(String url, String tag) {
        String result = "";
        switch (tag) {
            case YIMI_TEST:
                result = TEST_HOST + url;
                break;
            case YIMI_SERVER:
                result = SERVER_HOST + url;
                break;
        }
        return result;
    }
}
