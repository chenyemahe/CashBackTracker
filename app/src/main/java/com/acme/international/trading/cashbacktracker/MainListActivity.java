package com.acme.international.trading.cashbacktracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
    private Button mAdd;
    private Button mSummary;
    private Button mAmazon;
    // private AAListViewAdapter mAdapter;
    private AAExpandableListAdapter mExpandAdapter;
    private AAListDataHolder<AAProfile> mListHolder;
    private ArrayList<ArrayList<ArrayList<AAProfile>>> mExpandDataList;
    private ArrayList<ArrayList<AAProfile>> mChildList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_main_page);
        mListView = (ExpandableListView) findViewById(R.id.listView1);
        setViewClickListener();
        // mAdapter = new AAListViewAdapter(this, R.layout.aalistitem,
        // AAConstant.ADAPTER_ORIDER_LIST);
        mExpandAdapter = new AAExpandableListAdapter(AAUtils.EXPAND_ADAPTER_ORDER);
        mListView.setAdapter(mExpandAdapter);
        mAdd = (Button) findViewById(R.id.bt_add);
        mAdd.setOnClickListener(this);
        mSummary = (Button) findViewById(R.id.bt_summary);
        mSummary.setOnClickListener(this);
        mAmazon = (Button) findViewById(R.id.bt_amazon);
        mAmazon.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        // mListHolder = new
        // AAListDataHolder<>(AAManager.getManager().getDB().getAllProfile(getContentResolver()));
        // mAdapter.setDataHolder(mListHolder);
        super.onResume();
        setExpViewData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.edit_product_list) {
            startActivity(new Intent(this, ProductListPage.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                startActivity(new Intent(this, AddNewOrder.class));
                break;
            case R.id.bt_summary:
                startActivity(new Intent(this, AASummaryPage.class));
                break;
            case R.id.bt_amazon:
                startActivity(new Intent(this, ProductInventory.class));
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
        AAProfile profile = null;
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

    private void showItemMenu(final AAProfile profile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setItems(R.array.list_of_main, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemPos) {
                switch (itemPos) {
                    case 0:
                        break;
                    case 1:
                        AAManager.getManager().getDB().deleteAAProfile(getContentResolver(), profile);
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
        AAProfile profile = mChildList.get(groupPosition).get(childPosition);
        String itemId = profile.getProfileId();
        Intent intent = new Intent(this, AAItemListPage.class);
        intent.putExtra(AAUtils.INTENT_PROFILE_ID, itemId);
        startActivity(intent);
        return false;
    }

    private void setSignleLevelChildData() {
        if (mExpandDataList != null && mExpandDataList.size() != 0) {
            for (int i = 0; i < mExpandDataList.size(); i++) {
                if (mChildList.size() <= i) {
                    mChildList.add(new ArrayList<AAProfile>());
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
        mExpandDataList = AAUtils.sortProfileByDate(AAManager.getManager().getDB().getAllProfile(getContentResolver()));
        mChildList = new ArrayList<ArrayList<AAProfile>>();
        setSignleLevelChildData();
        mExpandAdapter.setListData(mExpandDataList, mChildList, this);
    }

}
