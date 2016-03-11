package com.acme.international.trading.cashbacktracker;

public class CashbackProfile {

    private String mId;
    private String mOrderId;
    private String mDate;
    private String mOrderStore;
    private String mOrderDetail;
    private String mCashbackCompany;
    private String mCashbackState;
    private String mCashbackPercent;
    private String mCashbackAmount;
    private String mCat;
    private String mOrderCost;

    public CashbackProfile() {
        mDate = null;
        mOrderStore = null;
        mOrderCost = null;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setOrderId(String id) {
        mOrderId = id;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setOrderStore(String store) {
        mOrderStore = store;
    }

    public void setOrderDetail(String detail) {
        mOrderDetail = detail;
    }

    public void setCashbackCompany(String company) {
        mCashbackCompany = company;
    }

    public void setCashbackState(String state) {
        mCashbackState = state;
    }

    public void setCashbackPercent(String percent) {
        mCashbackPercent = percent;
    }

    public void setCashbackAmount(String amount) {
        mCashbackAmount = amount;
    }

    public void setCat(String cat) {
        mCat = cat;
    }

    public void setOrderCost(String cost) {
        mOrderCost = cost;
    }

    public String getId() {
        return mId;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public String getDate() {
        return mDate;
    }

    public String getOrderStore() {
        return mOrderStore;
    }

    public String getOrderDetail() {
        return mOrderDetail;
    }

    public String getCashbackCompany() {
        return mCashbackCompany;
    }

    public String getCashbackState() {
        return mCashbackState;
    }

    public String getCashbackPercent() {
        return mCashbackPercent;
    }

    public String getCashbackAmount() {
        return mCashbackAmount;
    }

    public String getCat() {
        return mCat;
    }

    public String getCost() {
        return mOrderCost;
    }
}
