package com.zc741.sendmsg.http;

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
    public static final String SERVER_HOST = "http://immi-api.immistudy.com/";

    public static final String IMMI_TEST = "immi_test";
    public static final String IMMI_SERVER = "immi_server";

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
            case IMMI_TEST:
                result = TEST_HOST + url;
                break;
            case IMMI_SERVER:
                result = SERVER_HOST + url;
                break;
        }
        return result;
    }
}
