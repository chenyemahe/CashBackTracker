package com.acme.international.trading.cashbacktracker;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class AAProductListPage extends ListActivity{
    
    private ListView mListView;
    private AAProListViewAdapter mAdapter;
    private SharedPreferences mSharedPre;
    private ArrayList<View> mViewLoader;
    private AAProductListPage mProductListPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_product_list_page);
        mProductListPage = this;
        mListView = (ListView) findViewById(R.id.company_list);
        mAdapter = new AAProListViewAdapter();
        mListView.setAdapter(mAdapter);
    }
}
