package com.acme.international.trading.cashbacktracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.acme.amazon.listsupport.AAProListViewAdapter;

import java.util.ArrayList;
import java.util.Set;

public class AAProductListPage extends Activity{
    
    private LinearLayout mListView;
    private AAProListViewAdapter mAdapter;
    private SharedPreferences mSharedPre;
    private ArrayList<View> mViewLoader;
    private AAProductListPage mProductListPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_product_list_page);
        mProductListPage = this;
        setLayout();
    }
    
    private void setLayout() {
        mListView = (LinearLayout) findViewById(R.id.lly_product_list);
        mAdapter = new AAProListViewAdapter(mProductListPage);
        mSharedPre = getSharedPreferences(CbUtils.SHARED_PRE_NAME, 0);
        mViewLoader = new ArrayList<View>();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Set<String> productList = mSharedPre.getStringSet(CbUtils.SHARED_PRE_NAME, null);
        mAdapter.setDate(productList, this);
        refreshList();
    }
    
    public void refreshList() {
        if(mViewLoader.size() > mAdapter.getCount()) {
            for(int i = mAdapter.getCount(); i < mViewLoader.size(); i++) {
                mListView.removeView(mViewLoader.get(i));
            }
        }
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if(i >= mViewLoader.size()) {
                mViewLoader.add(mAdapter.getView(i, null, mListView));
                mListView.addView(mViewLoader.get(i));
            } else {
                mAdapter.getView(i, mViewLoader.get(i), mListView);
            }
        }
    }
    
}
