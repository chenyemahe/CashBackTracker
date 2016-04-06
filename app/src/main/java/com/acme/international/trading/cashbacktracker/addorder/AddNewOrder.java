package com.acme.international.trading.cashbacktracker.addorder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbManager;
import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewOrder extends Activity implements OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "AddNewOrder";

    private EditText mOrderId;
    private EditText mDate;
    private AutoCompleteTextView mOrderStore;
    private EditText mOrderDetail;
    private AutoCompleteTextView mCashbackCompany;
    private Spinner mCashbackState;
    private EditText mCashbackPercent;
    private EditText mCashbackAmount;
    private EditText mOrderCost;
    private EditText mCat;
    private Button mSubmit;

    private TableLayout add_layout;
    private TableLayout view_layout;
    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private String profileEditId;

    private final static String TYPE_STORE = "type_store";
    private final static String TYPE_CASHBACK = "type_cashback";

    public final static String INTENT_TYPE_EDIT = "intent_type_edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_order);
        profileEditId = getIntent().getStringExtra(AddNewOrder.INTENT_TYPE_EDIT);
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
        if (TextUtils.isEmpty(profileId)) {
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
        mDate.setOnFocusChangeListener(this);

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


        mCashbackState = (Spinner) findViewById(R.id.sp_cashback_state);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_of_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCashbackState.setAdapter(adapter);

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
                if (!mCashbackPercent.getText().toString().isEmpty()) {
                    String text = mCashbackPercent.getText().toString();
                    String percent = CbUtils.removeMark(text);
                    if (!CbUtils.isLess100(percent)) {
                        mCashbackPercent.setText("");
                        Toast.makeText(AddNewOrder.this.getApplicationContext(), getResources().getString(R.string.values_not_valid), Toast.LENGTH_LONG).show();
                    }
                    if (!text.contains("%")) {
                        String temp = text + "%";
                        mCashbackPercent.setText(temp);
                        mCashbackPercent.setSelection(temp.length() - 1);
                    }
                    if (!mOrderCost.getText().toString().isEmpty())
                        mCashbackAmount.setText(CbUtils.calCashbackAmount(mOrderCost.getText().toString(), percent));
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

                if (!CbUtils.removeMark(mCashbackPercent.getText().toString()).isEmpty()) {
                    mCashbackAmount.setText(CbUtils.calCashbackAmount(mOrderCost.getText().toString(), CbUtils.removeMark(mCashbackPercent.getText().toString())));
                }
            }
        });

        mCat = (EditText) findViewById(R.id.ed_cat);
        mCat.setOnClickListener(this);

        mSubmit = (Button) findViewById(R.id.bt_submit);
        mSubmit.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        if (!TextUtils.isEmpty(profileEditId))
            setEditProfileView(profileEditId);
    }

    private void setLayoutViewForView(String id) {
        add_layout = (TableLayout) findViewById(R.id.tl_add);
        add_layout.setVisibility(View.GONE);

        view_layout = (TableLayout) findViewById(R.id.tl_view);
        view_layout.setVisibility(View.VISIBLE);

        CashbackProfile profile = CbManager.getManager().getDB().getAAProfileById(getContentResolver(), id);

        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(getResources().getString(R.string.title_add_view_2));

        final TextView orderId = (TextView) findViewById(R.id.tv_order_id_3);
        TextView date = (TextView) findViewById(R.id.tv_date_3);
        final TextView orderStore = (TextView) findViewById(R.id.tv_store_3);
        final TextView orderDetail = (TextView) findViewById(R.id.tv_item_3);
        final TextView cashbackCompany = (TextView) findViewById(R.id.tv_cashback_websit_3);
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

        orderId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                orderId.requestFocus();
                return false;
            }
        });

        orderStore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                orderStore.requestFocus();
                return false;
            }
        });

        orderDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                orderDetail.requestFocus();
                return false;
            }
        });

        cashbackCompany.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cashbackCompany.requestFocus();
                return false;
            }
        });
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
        String orderCbState = mCashbackState.getSelectedItem().toString();
        String orderCbPercent = CbUtils.removeMark(mCashbackPercent.getText().toString());
        String orderCbAmount = mCashbackAmount.getText().toString();
        String orderCat = mCat.getText().toString();
        String orderCost = mOrderCost.getText().toString();

        if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(orderDate)
                || TextUtils.isEmpty(orderStore) || TextUtils.isEmpty(orderDetail) || TextUtils.isEmpty(orderCbCompany)
                || TextUtils.isEmpty(orderCbState) || TextUtils.isEmpty(orderCbPercent) || TextUtils.isEmpty(orderCbAmount)
                || TextUtils.isEmpty(orderCost)) {
            Toast.makeText(this,
                    getResources().getString(R.string.no_item_info),
                    Toast.LENGTH_LONG).show();
        } else {
            /*if (CbManager.getManager().getDB().getAAProfileById(getContentResolver(), orderId) != null) {
                Toast.makeText(this,
                        getResources().getString(R.string.existed_order),
                        Toast.LENGTH_LONG).show();
                return;
            }*/
            CashbackProfile profile;
            if (TextUtils.isEmpty(profileEditId)) {
                profile = new CashbackProfile();
            } else {
                profile = CbManager.getManager().getDB().getAAProfileById(getContentResolver(), profileEditId);
            }
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

            if (TextUtils.isEmpty(profileEditId)) {
                CbManager.getManager().getDB().saveCbProfile(getContentResolver(), profile);
            } else {
                CbManager.getManager().getDB().updateAAProfile(getContentResolver(), profile);
            }
            CbUtils.saveCustomKeyword(this, orderCbCompany, CbUtils.CASHBACK_KEYWORDS_LIST_WEBSITE);
            CbUtils.saveCustomKeyword(this, orderStore, CbUtils.CASHBACK_KEYWORDS_LIST_STORE);
            this.finish();
        }
    }

    private String[] getStringArray(String type) {
        String[] stringArrayFromRes = null;
        if (TextUtils.equals(type, TYPE_STORE)) {
            stringArrayFromRes = getResources().getStringArray(
                    R.array.list_of_store);
        } else if (TextUtils.equals(type, TYPE_CASHBACK)) {
            stringArrayFromRes = getResources().getStringArray(
                    R.array.list_of_cashback_website);
        }
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

    private void setEditProfileView(String id) {
        CashbackProfile profile = CbManager.getManager().getDB().getAAProfileById(getContentResolver(), id);
        mOrderId.setText(profile.getOrderId());
        mDate.setText(profile.getDate());
        mOrderStore.setText(profile.getOrderStore());
        mOrderDetail.setText(profile.getOrderDetail());
        mCashbackCompany.setText(profile.getCashbackCompany());
        mCashbackState.setSelection(getSpinnerIndex(profile.getCashbackState()));
        mCashbackPercent.setText(profile.getCashbackPercent());
        mCashbackAmount.setText(profile.getCashbackAmount());
        mOrderCost.setText(profile.getCost());
        mCat.setText(profile.getCat());
    }

    private int getSpinnerIndex(String s) {
        int index = 0;
        String[] array = CbUtils.getCbStateArray(this);
        for (int i = 0; i < array.length; i++) {
            if (TextUtils.equals(s, array[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
}
