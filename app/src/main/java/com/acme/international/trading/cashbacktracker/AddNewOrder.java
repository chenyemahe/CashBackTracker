package com.acme.international.trading.cashbacktracker;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

public class AddNewOrder extends Activity implements OnClickListener {

    private static final String TAG = "AddNewOrder";

    private EditText mOrderId;
    private EditText mDate;
    private AutoCompleteTextView mOrderStore;
    private EditText mOrderDetail;
    private AutoCompleteTextView mCashbackCompany;
    private EditText mCashbackState;
    private EditText mCashbackPercent;
    private EditText mCashbackAmount;
    private EditText mOrderCost;
    private Button mSubmit;

    private TableLayout add_layout;
    private TableLayout view_layout;

    private final static String TYPE_STORE = "type_store";
    private final static String TYPE_CASHBACK = "type_cashback";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_order);
        String profileId = getIntent().getStringExtra(CbUtils.INTENT_PROFILE_ID);
        if(TextUtils.isEmpty(profileId)) {
            setLayoutViewForAdding();
        } else {
            setLayoutViewForView(profileId);
        }
    }

    private void setLayoutViewForAdding() {
        view_layout = (TableLayout) findViewById(R.id.tl_view);
        view_layout.setVisibility(View.GONE);

        mOrderId = (EditText) findViewById(R.id.ed_order_id);
        mOrderId.setOnClickListener(this);

        mDate = (EditText) findViewById(R.id.ed_date);
        mDate.setOnClickListener(this);

        mOrderStore = (AutoCompleteTextView) findViewById(R.id.act_store);
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getStringArray(TYPE_STORE));
        mOrderStore.setAdapter(sAdapter);
        mOrderStore.setOnClickListener(this);

        mOrderDetail = (EditText) findViewById(R.id.ed_item);
        mOrderDetail.setOnClickListener(this);

        mCashbackCompany = (AutoCompleteTextView) findViewById(R.id.act_cashback_websit);
        ArrayAdapter<String> sAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getStringArray(TYPE_CASHBACK));
        mCashbackCompany.setAdapter(sAdapter2);
        mCashbackCompany.setOnClickListener(this);


        mCashbackState = (EditText) findViewById(R.id.ed_cashback_state);
        mCashbackState.setOnClickListener(this);

        mCashbackPercent = (EditText) findViewById(R.id.ed_cashback_rate);
        mCashbackPercent.setOnClickListener(this);
        mCashbackPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mOrderCost.getText().toString().isEmpty()) {
                    mCashbackAmount.setText(CbUtils.calCashbackAmount(mOrderCost.getText().toString(), mCashbackPercent.getText().toString()));
                }
            }
        });

        mCashbackAmount = (EditText) findViewById(R.id.ed_cashback_amount);
        mCashbackAmount.setOnClickListener(this);

        mOrderCost = (EditText) findViewById(R.id.ed_order_cost);
        mOrderCost.setOnClickListener(this);
        mOrderCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!mCashbackPercent.getText().toString().isEmpty()) {
                    mCashbackAmount.setText(CbUtils.calCashbackAmount(mOrderCost.getText().toString(), mCashbackPercent.getText().toString()));
                }
            }
        });

        mSubmit = (Button) findViewById(R.id.bt_submit);
        mSubmit.setOnClickListener(this);
    }

    private void setLayoutViewForView(String id) {
        add_layout = (TableLayout) findViewById(R.id.tl_add);
        add_layout.setVisibility(View.GONE);

        mSubmit = (Button) findViewById(R.id.bt_submit);
        mSubmit.setVisibility(View.GONE);

        CashbackProfile profile = CbManager.getManager().getDB().getAAProfileById(getContentResolver(), id);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                submitItem();
                break;

            default:
                break;
        }
    }

    private void submitItem() {
        String orderId = mOrderId.getText().toString();
        String orderDate = mDate.getText().toString();
        String orderStore = mOrderStore.getText().toString();
        String orderDetail = mOrderDetail.getText().toString();
        String orderCbCompany = mCashbackCompany.getText().toString();
        String orderCbState = mCashbackState.getText().toString();
        String orderCbPercent = mCashbackPercent.getText().toString();
        String orderCbAmount = mCashbackAmount.getText().toString();
        String orderCost = mOrderCost.getText().toString();

        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(orderDate)
                || TextUtils.isEmpty(orderStore)||TextUtils.isEmpty(orderDetail) || TextUtils.isEmpty(orderCbCompany)
                || TextUtils.isEmpty(orderCbState)||TextUtils.isEmpty(orderCbPercent) || TextUtils.isEmpty(orderCbAmount)
                || TextUtils.isEmpty(orderCost)) {
            Toast.makeText(this,
                    getResources().getString(R.string.no_item_info),
                    Toast.LENGTH_LONG).show();
        } else {
            CashbackProfile profile = new CashbackProfile();
            profile.setOrderId(orderId);
            profile.setDate(orderDate);
            profile.setOrderStore(orderStore);
            profile.setOrderDetail(orderDetail);
            profile.setCashbackCompany(orderCbCompany);
            profile.setCashbackState(orderCbState);
            profile.setCashbackPercent(orderCbPercent);
            profile.setCashbackAmount(orderCbAmount);
            profile.setOrderCost(orderCost);
            CbManager.getManager().getDB().saveCbProfile(getContentResolver(), profile);
        }
    }

    private String[] getStringArray(String type) {
        String[] stringArrayFromRes = getResources().getStringArray(
                R.array.list_of_item);
        return stringArrayFromRes;
    }
}
