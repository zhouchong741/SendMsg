package com.zc741.sendmsg.bean;

import io.realm.RealmObject;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/5/11 10:48.
 */
public class SentMessage extends RealmObject {

    public int messageId;
    public String iddCode;
    public String phoneNo;
    public String content;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getIddCode() {
        return iddCode;
    }

    public void setIddCode(String iddCode) {
        this.iddCode = iddCode;
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

    @Override
    public String toString() {
        return "SentMessage{" +
                "messageId=" + messageId +
                ", iddCode='" + iddCode + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
