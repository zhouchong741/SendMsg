package com.zc741.sendmsg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zc741.sendmsg.bean.SentMessage;
import com.zc741.sendmsg.db.RealmDao;

import java.util.List;

/**
 * 描述：
 * 作者：jiae
 * 时间：2018/5/11 10:59.
 */
public class SentMessageActivity extends AppCompatActivity {

    private List<SentMessage> mSentMessageList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_message);

        RecyclerView recyclerViewSentMessage = findViewById(R.id.recycler_sent_message);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSentMessage.setLayoutManager(linearLayoutManager);

        TextView tvEmpty = findViewById(R.id.empty);
        TextView tvTotal = findViewById(R.id.total);
        RealmDao realmDao = new RealmDao();
        mSentMessageList = realmDao.queryAll();
        if (mSentMessageList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvTotal.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            tvTotal.setVisibility(View.VISIBLE);
        }
        tvTotal.setText("总计: " + mSentMessageList.size());
        SentMessageAdapter adapter = new SentMessageAdapter(mSentMessageList);
        recyclerViewSentMessage.setAdapter(adapter);
    }

    public class SentMessageAdapter extends RecyclerView.Adapter<SentMessageHolder> {

        SentMessageAdapter(List<SentMessage> list) {
            super();
        }

        @NonNull
        @Override
        public SentMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SentMessageActivity.this).inflate(R.layout.item_sent_message, parent, false);
            return new SentMessageHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull SentMessageHolder holder, int position) {
            holder.mTvMessageId.setText("messageId: " + mSentMessageList.get(position).getMessageId());
            if (mSentMessageList.get(position).getIddCode().contains("+")) {
                holder.mTvPhoneNo.setText("电话: " + mSentMessageList.get(position).getIddCode() + mSentMessageList.get(position).getPhoneNo());
            } else {
                holder.mTvPhoneNo.setText("电话: " + "+" + mSentMessageList.get(position).getIddCode() + mSentMessageList.get(position).getPhoneNo());
            }
            holder.mTvContent.setText("内容: " + mSentMessageList.get(position).getContent());

        }

        @Override
        public int getItemCount() {
            return mSentMessageList.size();
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        private final TextView mTvPhoneNo;
        private final TextView mTvMessageId;
        private final TextView mTvContent;

        SentMessageHolder(View itemView) {
            super(itemView);
            mTvPhoneNo = itemView.findViewById(R.id.phone_number);
            mTvMessageId = itemView.findViewById(R.id.message_id);
            mTvContent = itemView.findViewById(R.id.content);
        }
    }
}
