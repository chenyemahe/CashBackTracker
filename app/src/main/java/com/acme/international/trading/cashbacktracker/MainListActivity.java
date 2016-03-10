package com.acme.international.trading.cashbacktracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * Created by ye1.chen on 3/3/16.
 */
public class MainListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener,
        ExpandableListView.OnChildClickListener {
    private ExpandableListView mListView;
    // private AAListViewAdapter mAdapter;
    private AAExpandableListAdapter mExpandAdapter;
    private ArrayList<ArrayList<ArrayList<CashbackProfile>>> mExpandDataList;
    private ArrayList<ArrayList<CashbackProfile>> mChildList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list);
        mListView = (ExpandableListView) findViewById(R.id.listView1);
        setViewClickListener();
        mExpandAdapter = new AAExpandableListAdapter(CbUtils.EXPAND_ADAPTER_ORDER);
        mListView.setAdapter(mExpandAdapter);
        Button mAdd = (Button) findViewById(R.id.bt_add);
        mAdd.setOnClickListener(this);
        Button mCbCompay = (Button) findViewById(R.id.bt_company);
        mCbCompay.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setExpViewData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                startActivity(new Intent(this, AddNewOrder.class));
                break;
            case R.id.bt_company:
                startActivity(new Intent(this, AAProductListPage.class));
                break;
            default:
                break;
        }

    }

    private void setViewClickListener() {
        mListView.setOnItemLongClickListener(this);
        mListView.setOnChildClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CashbackProfile profile = null;
        /*
         * if(mListHolder != null) { profile =
         * mListHolder.getList().get(position); }
         */
        if (view.getTag() instanceof AAListViewHodler) {
            AAListViewHodler holder = (AAListViewHodler) view.getTag();
            profile = mChildList.get(holder.getGroupId()).get(holder.getChildId());
            showItemMenu(profile);
            return true;
        }
        return false;
    }

    private void showItemMenu(final CashbackProfile profile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setItems(R.array.list_of_main, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemPos) {
                switch (itemPos) {
                    case 0:
                        break;
                    case 1:
                        CbManager.getManager().getDB().deleteAAProfile(getContentResolver(), profile);
                        setExpViewData();
                        mExpandAdapter.notifiListUpdate();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        CashbackProfile profile = mChildList.get(groupPosition).get(childPosition);
        String itemId = profile.getId();
        Intent intent = new Intent(this, AddNewOrder.class);
        intent.putExtra(CbUtils.INTENT_PROFILE_ID, itemId);
        startActivity(intent);
        return false;
    }

    private void setSignleLevelChildData() {
        if (mExpandDataList != null && mExpandDataList.size() != 0) {
            for (int i = 0; i < mExpandDataList.size(); i++) {
                if (mChildList.size() <= i) {
                    mChildList.add(new ArrayList<CashbackProfile>());
                }
                for (int j = mExpandDataList.get(i).size() - 1; j >= 0; j--) {
                    for (int k = 0; k < mExpandDataList.get(i).get(j).size(); k++) {
                        mChildList.get(i).add(mExpandDataList.get(i).get(j).get(k));
                    }
                }
            }
        }
    }

    private void setExpViewData() {
        mExpandDataList = CbUtils.sortProfileByDate(CbManager.getManager().getDB().getAllProfile(getContentResolver()));
        mChildList = new ArrayList<ArrayList<CashbackProfile>>();
        setSignleLevelChildData();
        mExpandAdapter.setListData(mExpandDataList, mChildList, this);
    }

}
