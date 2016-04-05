package com.acme.international.trading.cashbacktracker.maincb;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;


public class CbListViewHodler {

    //Order List
    private Context mContext;
    private TextView mOLDate;
    private TextView mOLStore;
    private TextView mOLCbCompany;
    private TextView mOLCbState;

    private TableRow row1;
    private TextView row2;

    private int groupId;
    private int childId;
    private String mStyle;

    public CbListViewHodler(String style, Context context) {
        mStyle = style;
        mContext = context;
    }

    public void setOrderListView(View v) {
        mOLDate = (TextView) v.findViewById(R.id.tv_date_2);
        mOLStore = (TextView) v.findViewById(R.id.tv_store_2);
        mOLCbCompany = (TextView) v.findViewById(R.id.tv_cashback_company_2);
        mOLCbState = (TextView) v.findViewById(R.id.tv_cashback_state_2);

        row1 = (TableRow) v.findViewById(R.id.tr_1);
        row2 = (TextView) v.findViewById(R.id.tr_2);
    }

    public void setData(CashbackProfile profile) {
        mOLDate.setText(profile.getDate());
        mOLStore.setText(profile.getOrderStore());
        mOLCbCompany.setText(profile.getCashbackCompany());
        String state = profile.getCashbackState();
        mOLCbState.setText(state);
        if (TextUtils.equals(state, CbUtils.getCbStateArray(mContext)[0])) {
            mOLCbState.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
        } else if (TextUtils.equals(state, CbUtils.getCbStateArray(mContext)[2])){
            mOLCbState.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_light));
        } else {
            mOLCbState.setTextColor(mContext.getResources().getColor(android.R.color.black));
        }
    }

    public void setExpandId(int group, int child) {
        groupId = group;
        childId = child;
    }

    public void setVisble(boolean state) {
        if (state) {
            row1.setVisibility(View.VISIBLE);
            row2.setVisibility(View.VISIBLE);
        } else {
            row1.setVisibility(View.GONE);
            row2.setVisibility(View.GONE);
        }
    }

    public int getGroupId() {
        return groupId;
    }

    public int getChildId() {
        return childId;
    }
}
