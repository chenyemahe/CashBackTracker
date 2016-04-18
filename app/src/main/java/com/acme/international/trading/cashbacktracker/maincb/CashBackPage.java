package com.acme.international.trading.cashbacktracker.maincb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.acme.international.trading.cashbacktracker.addorder.AddNewOrder;
import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbManager;
import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;
import com.acme.international.trading.cashbacktracker.database.AAProvider;
import com.acme.international.trading.cashbacktracker.database.CashbackDba;
import com.acme.international.trading.cashbacktracker.keywords.KeywordsSettingsPage;

import java.util.ArrayList;

/**
 * Created by ye1.chen on 3/3/16.
 */
public class CashBackPage extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener,
        ExpandableListView.OnChildClickListener, AdapterView.OnItemSelectedListener {
    private ExpandableListView mListView;
    // private AAListViewAdapter mAdapter;
    private CbExpandableListAdapter mExpandAdapter;
    private ArrayList<ArrayList<ArrayList<CashbackProfile>>> mExpandDataList;
    private ArrayList<ArrayList<CashbackProfile>> mChildList;

    private TextView mTotal;
    private boolean fistLauch;
    private Spinner mListPinner;

    private ContentObserver mDbObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            setCheckedData(mListPinner.getSelectedItemPosition());
            mExpandAdapter.notifiListUpdate();
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_cash_back_list);
        mListView = (ExpandableListView) findViewById(R.id.listView1);
        setViewClickListener();
        mExpandAdapter = new CbExpandableListAdapter(CbUtils.EXPAND_ADAPTER_ORDER);
        mListView.setAdapter(mExpandAdapter);
        Button mAdd = (Button) findViewById(R.id.bt_add);
        mAdd.setOnClickListener(this);
        Button mCbCompay = (Button) findViewById(R.id.bt_company);
        mCbCompay.setOnClickListener(this);
        mListPinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_of_view_selector, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListPinner.setAdapter(adapter);
        mListPinner.setOnItemSelectedListener(this);
        fistLauch = true;

        mTotal = (TextView) findViewById(R.id.tv_title_2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //set view data
        setExpViewData(null, null);
        if(fistLauch) {
            mListView.expandGroup(0);
            fistLauch = false;
        }
        String temp = "$ " + CbUtils.totalCashBack(this);
        mTotal.setText(temp);
        getContentResolver().registerContentObserver(AAProvider.ProfileColumns.CONTENT_URI, false,
                mDbObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mDbObserver);
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
                startActivity(new Intent(this, KeywordsSettingsPage.class));
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
        if (view.getTag() instanceof CbListViewHodler) {
            CbListViewHodler holder = (CbListViewHodler) view.getTag();
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
                        Intent intent_0 = new Intent(CashBackPage.this, AddNewOrder.class);
                        intent_0.putExtra(AddNewOrder.INTENT_TYPE_COPY, profile.getId());
                        startActivity(intent_0);
                        break;
                    case 1:
                        Intent intent = new Intent(CashBackPage.this, AddNewOrder.class);
                        intent.putExtra(AddNewOrder.INTENT_TYPE_EDIT, profile.getId());
                        startActivity(intent);
                        break;
                    case 2:
                        CbDialogFragment dialogFragment = new CbDialogFragment();
                        dialogFragment.setProfile(profile);
                        dialogFragment.show(getFragmentManager(), "missiles");
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

    private void setExpViewData(String type, String key) {
        ArrayList<CashbackProfile> profilesList = null;
        if (type == null) {
            profilesList = (ArrayList<CashbackProfile>) CbManager.getManager().getDB().getAllProfile(getContentResolver());
        } else if (TextUtils.equals(type, CashbackDba.PROFILE_SELECTION_BY_CASHBACK_STATE)) {
            profilesList = (ArrayList<CashbackProfile>) CbManager.getManager().getDB().getCbProfileBySelection
                    (getContentResolver(),CashbackDba.PROFILE_SELECTION_BY_CASHBACK_STATE, key);
        }
        mExpandDataList = CbUtils.sortProfileByDate(profilesList);
        if (mExpandDataList ==  null)
            return;
        mChildList = new ArrayList<ArrayList<CashbackProfile>>();
        setSignleLevelChildData();
        mExpandAdapter.setListData(mExpandDataList, mChildList, this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setCheckedData(position);
    }

    private void setCheckedData(int position) {
        switch (position) {
            case 0:
                //main view
                setExpViewData(null, null);
                mExpandAdapter.notifiListUpdate();
                break;
            //Pending list
            case 1:
                setExpViewData(CashbackDba.PROFILE_SELECTION_BY_CASHBACK_STATE, getResources().getStringArray(R.array.list_of_status)[1]);
                mExpandAdapter.notifiListUpdate();
                break;
            //Approved List
            case 2:
                setExpViewData(CashbackDba.PROFILE_SELECTION_BY_CASHBACK_STATE, getResources().getStringArray(R.array.list_of_status)[2]);
                mExpandAdapter.notifiListUpdate();
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
