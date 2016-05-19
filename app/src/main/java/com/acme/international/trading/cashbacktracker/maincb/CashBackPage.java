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
import android.widget.Toast;

import com.acme.international.trading.cashbacktracker.addorder.AddNewOrder;
import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbManager;
import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;
import com.acme.international.trading.cashbacktracker.database.AAProvider;
import com.acme.international.trading.cashbacktracker.database.CashbackDba;
import com.acme.international.trading.cashbacktracker.keywords.KeywordsSettingsPage;
import com.acme.international.trading.cashbacktracker.menu.MenuPage;

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
    private Spinner mSortPinner;
    private Spinner mListPinner;

    private final String type_sort = "sort";
    private final String type_view = "view";

    private ContentObserver mDbObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            setCheckedData(type_view, mListPinner.getSelectedItemPosition());
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
        Button mMenu = (Button) findViewById(R.id.bt_menu);
        mMenu.setOnClickListener(this);
        setSpinerView();
        fistLauch = true;

        mTotal = (TextView) findViewById(R.id.tv_title_2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSortPinner.setSelection(CbUtils.getSpinnerPrefs(this, CbUtils.PREFS_SPINNER_SORT_OPTION));
        mListPinner.setSelection(CbUtils.getSpinnerPrefs(this, CbUtils.PREFS_SPINNER_VIEW_OPTION));
        //set view data
        setExpViewData(mSortPinner.getSelectedItem().toString(), mListPinner.getSelectedItem().toString());
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
            case R.id.bt_menu:
                startActivity(new Intent(this, MenuPage.class));
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

    private void setExpViewData(String spinner_sort_key, String key) {
        key = CbUtils.keyMatchCbStatus(key, this);
        spinner_sort_key = CbUtils.keyMatchSort(spinner_sort_key, this);
        ArrayList<CashbackProfile> profilesList = null;
        if (key != null) {
            profilesList = (ArrayList<CashbackProfile>) CbManager.getManager().getDB().getCbProfileBySelection
                    (getContentResolver(), CashbackDba.PROFILE_SELECTION_BY_CASHBACK_STATE, key);
        } else {
            profilesList = (ArrayList<CashbackProfile>) CbManager.getManager().getDB().getAllProfile(getContentResolver());
        }
        if (spinner_sort_key == null) {
            mExpandDataList = CbUtils.sortProfileByDate(profilesList);
        } else if (TextUtils.equals(spinner_sort_key, CbUtils.MAIN_SPINNER_TYPE_BY_CB_STORE)) {
            mExpandDataList = CbUtils.sortProfileByKeyWords(CbUtils.MAIN_SPINNER_TYPE_BY_CB_STORE, profilesList);
        } else if (TextUtils.equals(spinner_sort_key, CbUtils.MAIN_SPINNER_TYPE_BY_CA)) {
            mExpandDataList = CbUtils.sortProfileByKeyWords(CbUtils.MAIN_SPINNER_TYPE_BY_CA, profilesList);
        } else if (TextUtils.equals(spinner_sort_key, CbUtils.MAIN_SPINNER_TYPE_BY_PAYMENT)) {
            mExpandDataList = CbUtils.sortProfileByKeyWords(CbUtils.MAIN_SPINNER_TYPE_BY_PAYMENT, profilesList);
        }
        if (mExpandDataList ==  null) {
            if (key != null) {
                Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.toast_no_list), Toast.LENGTH_LONG).show();
                mListPinner.setSelection(0);
            }
            return;
        }
        mChildList = new ArrayList<>();
        setSignleLevelChildData();
        mExpandAdapter.setListData(mExpandDataList, mChildList, this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner) {
            setCheckedData(type_view, position);
        } else if(parent.getId() == R.id.spinner_1) {
            setCheckedData(type_sort, position);
        }
    }

    private void setCheckedData(String main_type, int position) {
        if (TextUtils.equals(main_type,type_view)) {
            CbUtils.saveSpinnerPrefs(this, position, CbUtils.PREFS_SPINNER_VIEW_OPTION);
        } else if (TextUtils.equals(main_type,type_sort)) {
            CbUtils.saveSpinnerPrefs(this, position, CbUtils.PREFS_SPINNER_SORT_OPTION);
        }
        setExpViewData(mSortPinner.getSelectedItem().toString(), mListPinner.getSelectedItem().toString());
        mExpandAdapter.notifiListUpdate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setSpinerView() {
        mSortPinner = (Spinner) findViewById(R.id.spinner_1);
        ArrayAdapter<CharSequence> adapter_1 = ArrayAdapter.createFromResource(this,
                R.array.list_of_view_sorter, android.R.layout.simple_spinner_item);
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortPinner.setAdapter(adapter_1);
        mSortPinner.setOnItemSelectedListener(this);

        mListPinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(this,
                R.array.list_of_view_selector, android.R.layout.simple_spinner_item);
        adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListPinner.setAdapter(adapter_2);
        mListPinner.setOnItemSelectedListener(this);
    }
}
