package com.acme.international.trading.cashbacktracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class AAProListViewAdapter {

    private Set<String> mStringData;
    private ArrayList<String> listData;
    private ArrayList<Integer> listEditCT;
    private SharedPreferences mSharedPre;
    private AAProductListPage parentPage;
    private Context mContext;
    private int mFocusItemNum;

    public AAProListViewAdapter(AAProductListPage page) {
        parentPage = page;
    }

    public int getCount() {
        if (listData == null)
            return 0;
        return listData.size();
    }

    public Object getItem(int position) {
        if (listData == null)
            return null;
        return listData.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.aa_pro_list_item, parent, false);
        }
        StringDataHolder holder = null;
        if (convertView.getTag() instanceof StringDataHolder) {
            holder = (StringDataHolder) convertView.getTag();
        }
        if (holder == null)
            holder = new StringDataHolder(convertView);
        if (TextUtils.isEmpty(listData.get(position))) {
            listEditCT.set(position, CbUtils.PRODUCT_LIST_EDIT_STATE);
        } else if (listEditCT.get(position) == CbUtils.PRODUCT_LIST_INI_STATE) {
            listEditCT.set(position, CbUtils.PRODUCT_LIST_SHOW_STATE);
        }

        holder.setDate(position);
        convertView.setTag(holder);
        return convertView;
    }

    public void setDate(Set<String> data, Context context) {
        mStringData = data;
        listData = CbUtils.setToArrayList(mStringData);
        if (listData == null) {
            listData = new ArrayList<String>();
        }
        if (listEditCT == null) {
            listEditCT = new ArrayList<Integer>();
            for (int i = 0; i < listData.size(); i++) {
                listEditCT.add(CbUtils.PRODUCT_LIST_INI_STATE);
            }
        }
        addLastRow();
        mContext = context;
        mSharedPre = mContext.getSharedPreferences(CbUtils.SHARED_PRE_NAME, 0);
    }

    public void notifiListUpdate() {
        parentPage.refreshList();
    }

    private void addLastRow() {
        if (listData.size() == 0 || !TextUtils.equals(listData.get(listData.size() - 1), "")) {
            listEditCT.add(CbUtils.PRODUCT_LIST_INI_STATE);
            mFocusItemNum = listData.size();
            listData.add("");
        }
    }

    private class StringDataHolder implements OnClickListener{
        private int index;
        private RelativeLayout layoutEdit;
        private RelativeLayout layoutShow;
        private TextView tv;
        private EditText et;
        private Button add;
        private Button edit;
        private View v;

        public StringDataHolder(View view) {
            v = view;
        }

        public void setDate(int position) {
            this.index = position;
            layoutEdit = (RelativeLayout) v.findViewById(R.id.ly_edit_product);
            layoutShow = (RelativeLayout) v.findViewById(R.id.ly_show_product);
            tv = (TextView) v.findViewById(R.id.tv_product_name);
            et = (EditText) v.findViewById(R.id.ed_product);
            add = (Button) v.findViewById(R.id.bt_add);
            edit = (Button) v.findViewById(R.id.bt_edit);

            switch (listEditCT.get(index)) {

            case CbUtils.PRODUCT_LIST_EDIT_STATE:
                layoutEdit.setVisibility(View.VISIBLE);
                et.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                layoutShow.setVisibility(View.GONE);
                tv.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                break;
            case CbUtils.PRODUCT_LIST_SHOW_STATE:
                layoutEdit.setVisibility(View.GONE);
                et.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                layoutShow.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                break;
            default:
                break;
            }
            tv.setText(listData.get(position));
            et.setText(listData.get(position));
            tv.setOnClickListener(this);
            et.setOnClickListener(this);
            if (this.index == mFocusItemNum && !et.hasFocus()) {
                et.requestFocus();
                et.setSelection(et.length());
            }
            add.setOnClickListener(this);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.bt_add:
                String productName = et.getText().toString();
                if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(mContext, "Please Enter the Product Name", Toast.LENGTH_LONG).show();
                    break;
                }
                listData.set(index, et.getText().toString());
                saveDataToPre();
                // add for next new product list
                addLastRow();
                listEditCT.set(index, CbUtils.PRODUCT_LIST_SHOW_STATE);
                notifiListUpdate();
                mFocusItemNum = listData.size() - 1;
                break;

            case R.id.bt_edit:
                listEditCT.set(index, CbUtils.PRODUCT_LIST_EDIT_STATE);
                mFocusItemNum = index;
                notifiListUpdate();
                break;

            case R.id.ed_product:
                if (!et.hasFocus()) {
                    et.requestFocus();
                    et.setSelection(et.length());
                }
                break;
            case R.id.tv_product_name:
                showItemMenu();
            default:
                break;
            }
        }

        private void saveDataToPre() {
            Set<String> product_Set = CbUtils.arrayListToSet(listData);
            product_Set.remove("");
            mSharedPre.edit().putStringSet(CbUtils.SHARED_PRE_NAME, product_Set).commit();
        }

        private void showItemMenu() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
            builder.setItems(R.array.list_of_product, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int itemPos) {
                    switch (itemPos) {
                    case 0:
                        listData.remove(index);
                        saveDataToPre();
                        notifiListUpdate();
                        break;
                    }
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
}
