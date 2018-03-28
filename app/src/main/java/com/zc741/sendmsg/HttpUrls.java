package com.zc741.sendmsg;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/3/28 11:32.
 */

public class HttpUrls {

    public static final String SERVER_HOST = "http://www.zc741.com/";

    public static final String GET_INFO = "";


    public static final String YIMI = "yimi";

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
