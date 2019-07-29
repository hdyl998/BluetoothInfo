package com.hdyl.bttech;

import android.content.Context;

import com.hdyl.bttech.adapter.BaseViewHolder;
import com.hdyl.bttech.adapter.IMultiItemViewType;
import com.hdyl.bttech.adapter.SuperAdapter;

import java.util.List;

/**
 * Created by liugd on 2019/7/16.
 */

public class MsgAdapter extends SuperAdapter<MsgItem> {
    public MsgAdapter(Context context, List<MsgItem> data) {
        super(context, data, new IMultiItemViewType<MsgItem>() {
            @Override
            public int[] createLayoutIds() {
                return new int[]{R.layout.item_msg_left, R.layout.item_msg_right};
            }

            @Override
            public int getLayoutIDsIndex(MsgItem msgItem, int position) {
                return msgItem.isMine() ? 1 : 0;
            }
        });
    }

    @Override
    protected void onBind(BaseViewHolder holder, MsgItem item, int position) {
        holder.setText(R.id.tvTitle, item.isMine() ? "我" : "对方");
        holder.setText(R.id.tvContent, item.getMsg());
    }

}



