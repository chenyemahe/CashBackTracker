package com.acme.international.trading.cashbacktracker;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.acme.amazon.AAItem;
import com.acme.amazon.AAProfile;

public class AAListViewHodler {

    //Order List
    private TextView mOLDate;
    private TextView mOLTitle;
    private TextView mOLTotal;
    //Item List
    private TextView mItemName;
    private TextView mItemQuantity;
    private TextView mItemCost;

    private String mStyle;

    private int groupId;
    private int childId;

    public AAListViewHodler(String style) {
        mStyle = style;
    }

    public void setOrderListView(View v) {
        mOLDate = (TextView) v.findViewById(R.id.tv_date);
        mOLTitle = (TextView) v.findViewById(R.id.tv_title);
        mOLTotal = (TextView) v.findViewById(R.id.tv_cost);
    }

    public void setItemListView(View v) {
        mItemName = (TextView) v.findViewById(R.id.tv_name);
        mItemQuantity = (TextView) v.findViewById(R.id.tv_quantity);
        mItemCost = (TextView) v.findViewById(R.id.tv_cost);
        if (TextUtils.equals(mStyle, CbUtils.EXPAND_ADAPTER_FBA)) {
            mItemCost.setVisibility(View.GONE);
        }
    }

    public void setData(CashbackProfile profile) {
        mOLDate.setText(profile.getDate());
        mOLTitle.setText(profile.getTitle());
        mOLTotal.setText("$ " + profile.getCost());
    }

    public void setData(AAItem item) {
        mItemName.setText(" - " + item.getName());
        mItemQuantity.setText(" : " + String.valueOf(item.getQuality()));
        mItemCost.setText("Total Cost : " + item.getCost());
    }

    public void setExpandId(int group, int child) {
        groupId = group;
        childId = child;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getChildId() {
        return childId;
    }
}
