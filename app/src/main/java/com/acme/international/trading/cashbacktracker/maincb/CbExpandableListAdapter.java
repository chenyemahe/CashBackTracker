
package com.acme.international.trading.cashbacktracker.maincb;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;

import java.util.ArrayList;

public class CbExpandableListAdapter extends BaseExpandableListAdapter {

    private String mStyle;

    private ArrayList<ArrayList<ArrayList<CashbackProfile>>> mOrderList;


    private ArrayList<String> mGroupNameList;

    private ArrayList<ArrayList<CashbackProfile>> mChildList;

    private Context mContext;

    private static final int GROUP_PADDING = 10;

    public CbExpandableListAdapter(String style) {
        mStyle = style;
    }

    @Override
    public int getGroupCount() {
        if (TextUtils.equals(mStyle, CbUtils.EXPAND_ADAPTER_ORDER)) {
            if (mOrderList == null)
                return 0;
            else
                return mOrderList.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getSignleLevelChildNum(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (TextUtils.equals(mStyle, CbUtils.EXPAND_ADAPTER_ORDER)) {
            if (mOrderList == null)
                return null;
            return mOrderList.get(groupPosition);
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (TextUtils.equals(mStyle, CbUtils.EXPAND_ADAPTER_ORDER)) {
            if (mChildList == null)
                return null;
            return mChildList.get(groupPosition).get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        TextView text = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cb_group_view, parent,
                    false);
            convertView.setLongClickable(false);
        }
        text = (TextView) convertView.findViewById(R.id.tv_group_name);
        text.setText(mGroupNameList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cb_list_item_view, parent, false);
        }
        CbListViewHodler holder = new CbListViewHodler(mStyle, mContext);
        holder.setOrderListView(convertView);
        if(childPosition == 0) {
            holder.setVisble(true);
        } else {
            holder.setVisble(false);
        }
        if (TextUtils.equals(mStyle, CbUtils.EXPAND_ADAPTER_ORDER)) {
            holder.setData(mChildList.get(groupPosition).get(childPosition));
        }
        holder.setExpandId(groupPosition, childPosition);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    private int getSignleLevelChildNum(int groupPosition) {
        if (mChildList == null)
            return 0;
        int childNum = 0;
        if (TextUtils.equals(mStyle, CbUtils.EXPAND_ADAPTER_ORDER)) {
            if (mChildList.get(groupPosition).size() != 0) {
                childNum = mChildList.get(groupPosition).size();
            }
        }
        return childNum;
    }

    public void setListData(ArrayList<ArrayList<ArrayList<CashbackProfile>>> list,
            ArrayList<ArrayList<CashbackProfile>> childList, Context context) {
        String temp = null;
        mOrderList = list;
        mContext = context;
        mGroupNameList = new ArrayList<String>();
        mChildList = childList;
        for (int i = 0; i < mOrderList.size(); i++) {
            for (int j = 0; j < mOrderList.get(i).size(); j++) {
                if (mOrderList.get(i).get(j).size() != 0) {
                    String keySort = CbUtils.keyMatchSort(
                            mContext.getResources().getStringArray(R.array.list_of_view_sorter)[CbUtils.getSpinnerPrefs(mContext, CbUtils.PREFS_SPINNER_SORT_OPTION)],mContext);
                    if (keySort == null) {
                        temp = mOrderList.get(i).get(j).get(0).getDate().split("/")[2];
                    } else if(TextUtils.equals(keySort, CbUtils.MAIN_SPINNER_TYPE_BY_CB_STORE)) {
                        temp = mOrderList.get(i).get(j).get(0).getCashbackCompany();
                    } else if(TextUtils.equals(keySort, CbUtils.MAIN_SPINNER_TYPE_BY_CA)) {
                        temp = mOrderList.get(i).get(j).get(0).getCat();
                    } else if(TextUtils.equals(keySort, CbUtils.MAIN_SPINNER_TYPE_BY_PAYMENT)) {
                        temp = mOrderList.get(i).get(j).get(0).getPaymentFrom();
                    }
                    if(TextUtils.isEmpty(temp))
                        temp = mContext.getResources().getString(R.string.ed_default);
                    mGroupNameList.add(temp);
                    break;
                }
            }
        }
        notifiListUpdate();
    }

    public void notifiListUpdate() {
        notifyDataSetChanged();
    }
}
