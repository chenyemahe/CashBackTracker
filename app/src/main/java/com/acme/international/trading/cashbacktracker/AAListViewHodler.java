package com.acme.international.trading.cashbacktracker;

import android.view.View;
import android.widget.TextView;


public class AAListViewHodler {

    //Order List
    private TextView mOLDate;
    private TextView mOLStore;
    private TextView mOLCbCompany;
    private TextView mOLCbState;

    private int groupId;
    private int childId;
    private String mStyle;

    public AAListViewHodler(String style) {
        mStyle = style;
    }

    public void setOrderListView(View v) {
        mOLDate = (TextView) v.findViewById(R.id.tv_date_2);
        mOLStore = (TextView) v.findViewById(R.id.tv_store_2);
        mOLCbCompany = (TextView) v.findViewById(R.id.tv_cashback_company_2);
        mOLCbState = (TextView) v.findViewById(R.id.tv_cashback_state_2);
    }

    public void setData(CashbackProfile profile) {
        mOLDate.setText(profile.getDate());
        mOLStore.setText(profile.getOrderStore());
        mOLCbCompany.setText(profile.getCashbackCompany());
        mOLCbState.setText(profile.getCashbackState());
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
