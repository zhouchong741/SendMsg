package com.zc741.sendmsg.db;

import com.zc741.sendmsg.bean.SentMessage;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/5/11 14:28.
 */
public class RealmDao {


    /**
     * 查询单条记录是否存在 1.true 存在 2.false 不存在
     *
     * @param messageId messageId
     * @return
     */
    public boolean isMessageIdExist(int messageId) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(SentMessage.class).equalTo("messageId", messageId).findFirst() != null;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<SentMessage> queryAll() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SentMessage> realmResults = realm.where(SentMessage.class).findAll();
        return realm.copyFromRealm(realmResults);
    }

}
