package com.zc741.sendmsg.bean;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/3/28 11:31.
 */

public class PhoneNumber {
    public long phoneNumber;
    public String content;

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PhoneNumber(long phoneNumber, String content) {
        this.phoneNumber = phoneNumber;
        this.content = content;
    }
}
