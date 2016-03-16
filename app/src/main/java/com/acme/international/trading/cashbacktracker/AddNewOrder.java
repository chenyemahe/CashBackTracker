package com.acme.international.trading.cashbacktracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewOrder extends Activity implements OnClickListener,View.OnFocusChangeListener {

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
    private EditText mCat;
    private Button mSubmit;

    private TableLayout add_layout;
    private TableLayout view_layout;
    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    private final static String TYPE_STORE = "type_store";
    private final static String TYPE_CASHBACK = "type_cashback";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_order);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        mDate = (EditText) findViewById(R.id.ed_date_mm);
        mDate.setOnClickListener(this);
        mDate.setOnFocusChangeListener(this);;

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

        mCat = (EditText) findViewById(R.id.ed_cat);
        mCat.setOnClickListener(this);

        mSubmit = (Button) findViewById(R.id.bt_submit);
        mSubmit.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setLayoutViewForView(String id) {
        add_layout = (TableLayout) findViewById(R.id.tl_add);
        add_layout.setVisibility(View.GONE);

        view_layout = (TableLayout) findViewById(R.id.tl_view);
        view_layout.setVisibility(View.VISIBLE);

        CashbackProfile profile = CbManager.getManager().getDB().getAAProfileById(getContentResolver(), id);

        TextView orderId = (TextView) findViewById(R.id.tv_order_id_3);
        TextView date = (TextView) findViewById(R.id.tv_date_3);
        TextView orderStore = (TextView) findViewById(R.id.tv_store_3);
        TextView orderDetail = (TextView) findViewById(R.id.tv_item_3);
        TextView cashbackCompany = (TextView) findViewById(R.id.tv_cashback_websit_3);
        TextView cashbackState = (TextView) findViewById(R.id.tv_cashback_state_3);
        TextView cashbackPercent = (TextView) findViewById(R.id.tv_cashback_rate_3);
        TextView cashbackAmount = (TextView) findViewById(R.id.tv_cashback_amount_3);
        TextView orderCost = (TextView) findViewById(R.id.tv_order_cost_3);
        TextView cat = (TextView) findViewById(R.id.tv_cat_3);

        orderId.setText(profile.getOrderId());
        date.setText(profile.getDate());
        orderStore.setText(profile.getOrderStore());
        orderDetail.setText(profile.getOrderDetail());
        cashbackCompany.setText(profile.getCashbackCompany());
        cashbackState.setText(profile.getCashbackState());
        cashbackPercent.setText(profile.getCashbackPercent());
        cashbackAmount.setText(profile.getCashbackAmount());
        orderCost.setText(profile.getCost());
        cat.setText(profile.getCat());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                submitItem();
                break;
            case R.id.ed_date_mm:
                mDatePickerDialog.show();
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
        String orderCat = mCat.getText().toString();
        String orderCost = mOrderCost.getText().toString();

        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(orderDate)
                || TextUtils.isEmpty(orderStore)||TextUtils.isEmpty(orderDetail) || TextUtils.isEmpty(orderCbCompany)
                || TextUtils.isEmpty(orderCbState)||TextUtils.isEmpty(orderCbPercent) || TextUtils.isEmpty(orderCbAmount)
                || TextUtils.isEmpty(orderCost)) {
            Toast.makeText(this,
                    getResources().getString(R.string.no_item_info),
                    Toast.LENGTH_LONG).show();
        } else {
            if(CbManager.getManager().getDB().getAAProfileById(getContentResolver(),orderId) != null) {
                Toast.makeText(this,
                        getResources().getString(R.string.existed_order),
                        Toast.LENGTH_LONG).show();
                return;
            }
            CashbackProfile profile = new CashbackProfile();
            profile.setOrderId(orderId);
            profile.setDate(orderDate);
            profile.setOrderStore(orderStore);
            profile.setOrderDetail(orderDetail);
            profile.setCashbackCompany(orderCbCompany);
            profile.setCashbackState(orderCbState);
            profile.setCashbackPercent(orderCbPercent);
            profile.setCashbackAmount(orderCbAmount);
            profile.setCat(orderCat);
            profile.setOrderCost(orderCost);
            CbManager.getManager().getDB().saveCbProfile(getContentResolver(), profile);
            this.finish();
        }
    }

    private String[] getStringArray(String type) {
        String[] stringArrayFromRes = getResources().getStringArray(
                R.array.list_of_item);
        return stringArrayFromRes;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            if (v.getId() == R.id.ed_date_mm) {
                mDatePickerDialog.show();
            }
        }
    }
}
