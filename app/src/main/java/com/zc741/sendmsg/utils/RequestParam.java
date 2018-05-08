package com.zc741.sendmsg.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestParam {

    private Map<String, String> mParams;

    public RequestParam() {
        this(null);
    }

    public RequestParam(Map<String, String> params) {
        if (params == null) {
            mParams = new HashMap<>();
        } else {
            mParams = new HashMap<>(params);
        }
    }

    public void put(String key, String value) {
        mParams.put(key, value);
    }

    public void put(String key, int value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, char value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, double value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, float value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, long value) {
        put(key, String.valueOf(value));
    }

    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return mParams == null || mParams.isEmpty();
    }

    public String toParamString() {
        StringBuilder builder = new StringBuilder();
        builder.append("?");
        Set<String> keys = mParams.keySet();
        for (String key : keys) {
            builder.append(key)
                    .append("=")
                    .append(mParams.get(key))
                    .append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public Map<String, String> get() {
        return mParams;
    }
}