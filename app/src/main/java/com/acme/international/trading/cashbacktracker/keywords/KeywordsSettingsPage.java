package com.acme.international.trading.cashbacktracker.keywords;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;

/**
 * Created by ye1.chen on 3/29/16.
 */
public class KeywordsSettingsPage extends Activity implements View.OnClickListener {

    private TextView mAdd;
    private ListView mListView;
    private KeywordsSettingsAdapter mAdapter;
    private EditText mEt;

    private Button mCashback;
    private Button mStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keywords_settings);
        mAdd = (TextView) findViewById(R.id.tv_keywords_add);
        mAdd.setOnClickListener(this);
        mEt = (EditText) findViewById(R.id.ed_keywords_add);
        mAdapter = new KeywordsSettingsAdapter(this, KeywordsSettingsAdapter.CASHBACK_WEB);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mAdapter.setData();

        mCashback = (Button) findViewById(R.id.bt_cashback);
        mCashback.setOnClickListener(this);
        mCashback.setBackgroundResource(R.color.button_color);
        mStore = (Button) findViewById(R.id.bt_store);
        mStore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_keywords_add:
                String key = mEt.getText().toString();
                if (!TextUtils.isEmpty(key)) {
                    if (TextUtils.equals(mAdapter.getStyle(), KeywordsSettingsAdapter.CASHBACK_WEB)) {
                        CbUtils.saveCustomKeyword(this, key, CbUtils.CASHBACK_KEYWORDS_LIST_WEBSITE);
                    } else if (TextUtils.equals(mAdapter.getStyle(), KeywordsSettingsAdapter.STORE)) {
                        CbUtils.saveCustomKeyword(this, key, CbUtils.CASHBACK_KEYWORDS_LIST_STORE);
                    }
                    mAdapter.refresh();
                }
                break;
            case R.id.bt_cashback:
                mAdapter.setStyle(KeywordsSettingsAdapter.CASHBACK_WEB);
                mCashback.setBackgroundResource(R.color.button_color);
                mStore.setBackgroundResource(R.color.color_background);
                mAdapter.refresh();
                break;
            case R.id.bt_store:
                mAdapter.setStyle(KeywordsSettingsAdapter.STORE);
                mCashback.setBackgroundResource(R.color.color_background);
                mStore.setBackgroundResource(R.color.button_color);
                mAdapter.refresh();
                break;
            default:
                break;
        }
        if (v.getId() == R.id.tv_keywords_add) {
        }
    }
}
