package com.zc741.sendmsg;

/**
 * Created by jiae on 2018/3/28.
 */

public class HttpUrls {

    private static final String SERVER_HOST = "http://www.zc741.com/";

    private static final String GET_INFO = "siyuanzaixian/datas.json";


    private static final String YIMI = "yimi";

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
