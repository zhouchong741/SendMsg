package com.zc741.sendmsg.bean;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/3/28 11:31.
 */

public class PhoneNumber {
    public String phoneNo;
    public int messageId;
    public String content;


    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PhoneNumber(String phoneNo, String content, int messageId) {
        this.phoneNo = phoneNo;
        this.content = content;
        this.messageId = messageId;
    }
}
