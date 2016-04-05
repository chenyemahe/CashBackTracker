
package com.acme.international.trading.cashbacktracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;


import com.acme.international.trading.cashbacktracker.database.AAProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CbUtils {

    public static final String INTENT_PROFILE_ID = "Item_ID";

    public static final String INTENT_PRODUCT_NAME = "product_name";

    public static final String UNSORT = "unsort";

    public static final String SHARED_PRE_NAME = "aa_shared_pre_name";

    public static final int PRODUCT_LIST_INI_STATE = -1;

    public static final int PRODUCT_LIST_EDIT_STATE = 1;

    public static final int PRODUCT_LIST_SHOW_STATE = 0;

    public static final double RATE_BV = 0.15;

    public static final double RATE_AMAZON_REF_HEALTHY = 0.15;

    public static final String EXPAND_ADAPTER_ORDER = "expand_adapter_order";

    public static final String EXPAND_ADAPTER_FBA = "expand_adapter_fba";

    public static final String INTENT_EXTRA_ITEM_STYLE = "intent_extra_item_style";

    public static final String CASHBACK_PREFS = "cashback_prefs";
    public static final String CASHBACK_KEYWORDS_LIST_WEBSITE = "cashback_keywords_list_website";
    public static final String CASHBACK_KEYWORDS_LIST_STORE = "cashback_keywords_list_store";

    public static void toContentValues(CashbackProfile profile, ContentValues values) {
        values.put(AAProvider.ProfileColumns.ORDER_ID, profile.getOrderId());
        values.put(AAProvider.ProfileColumns.ORDER_DATE, profile.getDate());
        values.put(AAProvider.ProfileColumns.ORDER_STORE, profile.getOrderStore());
        values.put(AAProvider.ProfileColumns.ORDER_DETAIL, profile.getOrderStore());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_COMPANY, profile.getCashbackCompany());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_STATE, profile.getCashbackState());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_PERCENT, profile.getCashbackPercent());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_AMOUNT, profile.getCashbackAmount());
        values.put(AAProvider.ProfileColumns.ORDER_CATEGORY, profile.getCat());
        values.put(AAProvider.ProfileColumns.ORDER_TOTAL_COST, profile.getCost());
    }

    public static void fromCursor(Cursor cursor, CashbackProfile profile) {
        int idxId = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns._ID);
        int idxOrderId = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_ID);
        int idxDate = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_DATE);
        int idxStore = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_STORE);
        int idxDetail = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_DETAIL);
        int idxCashbackCompany = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_CASHBACK_COMPANY);
        int idxCashbackState = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_CASHBACK_STATE);
        int idxCashbackPercent = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_CASHBACK_PERCENT);
        int idxCashbackAmount = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_CASHBACK_AMOUNT);
        int idxCategory = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_CATEGORY);
        int idxCost = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_TOTAL_COST);

        profile.setId(cursor.getString(idxId));
        profile.setOrderId(cursor.getString(idxOrderId));
        profile.setDate(cursor.getString(idxDate));
        profile.setOrderStore(cursor.getString(idxStore));
        profile.setOrderDetail(cursor.getString(idxDetail));
        profile.setCashbackCompany(cursor.getString(idxCashbackCompany));
        profile.setCashbackState(cursor.getString(idxCashbackState));
        profile.setCashbackPercent(cursor.getString(idxCashbackPercent));
        profile.setCashbackAmount(cursor.getString(idxCashbackAmount));
        profile.setCat(cursor.getString(idxCategory));
        profile.setOrderCost(cursor.getString(idxCost));
    }

    /**
     * Sort order profile list by date
     * 
     * @param profileList
     * @return ArrayList by key year and month for sorted list
     */
    public static synchronized ArrayList<ArrayList<ArrayList<CashbackProfile>>> sortProfileByDate(
            List<CashbackProfile> profileList) {
        if (profileList == null)
            return null;
        ArrayList<ArrayList<ArrayList<CashbackProfile>>> sortListMap = new ArrayList<ArrayList<ArrayList<CashbackProfile>>>();
        ArrayList<String> yearList = new ArrayList<String>();
        String year = UNSORT;
        String month = UNSORT;
        String day = UNSORT;
        // date structure Month/Date/Year
        for (CashbackProfile profile : profileList) {
            String[] mdate = profile.getDate().split("/");
            if (mdate.length == 3) {
                year = mdate[2];
                month = mdate[0];
                day = mdate[1];
            } else {
                return null;
            }
            int indexYear = -1;
            int indexMonth = Integer.parseInt(month);
            if (!yearList.contains(year)) {
                for (int i = 0; i <= yearList.size(); i++) {
                    if (i == yearList.size()) {
                        sortListMap.add(new ArrayList<ArrayList<CashbackProfile>>());
                        yearList.add(year);
                        break;
                    }
                    if (Integer.parseInt(yearList.get(i)) < Integer.parseInt(year)) {
                        sortListMap.add(i, new ArrayList<ArrayList<CashbackProfile>>());
                        yearList.add(i, year);
                        break;
                    }
                }
                indexYear = findStElemInArray(yearList, year);
                for (int i = 0; i < 12; i++) {
                    sortListMap.get(indexYear).add(new ArrayList<CashbackProfile>());
                }
            } else {
                indexYear = findStElemInArray(yearList, year);
            }

            ArrayList<CashbackProfile> list = sortListMap.get(indexYear).get(indexMonth);
            for (int i = 0; i <= list.size(); i++) {
                if (i == list.size()) {
                    list.add(profile);
                    break;
                }
                // sort by latest date
                if (Integer.parseInt(list.get(i).getDate().split("/")[1]) < Integer.parseInt(day)) {
                    list.add(i, profile);
                    break;
                }
            }
        }
        return sortListMap;
    }

    private static int findStElemInArray(ArrayList<String> list, String item) {
        int i = 0;
        for (String s : list) {
            if (s.equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Parse Set to ArrayList
     * 
     * @param set
     * @return ArrayList
     */
    public static <T> ArrayList<T> setToArrayList(Set<T> set) {
        if (set == null)
            return null;
        ArrayList<T> tArray = new ArrayList<T>();
        Iterator<T> it = set.iterator();
        while (it.hasNext()) {
            T t = it.next();
            tArray.add(t);
        }
        return tArray;
    }

    /**
     * Parse ArrayList to Set
     *
     * @return Set
     */
    public static <T> Set<T> arrayListToSet(ArrayList<T> list) {
        if (list == null)
            return null;
        Set<T> set = new HashSet<T>();
        for (T t : list) {
            set.add(t);
        }
        return set;
    }

    public static String getTotalProCost(ArrayList<CashbackProfile> proList) {
        Double cost = 0.0;
        for (CashbackProfile profile : proList) {
            cost += Double.parseDouble(profile.getCost());
        }
        return String.valueOf(cost);
    }

    public static String calCashbackAmount(String totalCost, String rate) {
        if (TextUtils.isEmpty(totalCost) || TextUtils.isEmpty(rate)) {
            return "0";
        }
        return String.valueOf(Double.parseDouble(totalCost) * Double.parseDouble(rate));
    }

    public static String[] getCbStateArray(Context context) {
        return context.getResources().getStringArray(R.array.list_of_status);
    }

    public static boolean saveCustomKeyword(Context context, String keywords, String type_key) {
        SharedPreferences prefs = context.getSharedPreferences(CASHBACK_PREFS, 0);
        String newList = getCustomKeywordList(context, type_key);
        if (TextUtils.isEmpty(newList)) {
            newList = keywords;
        } else {
            newList = newList + "," + keywords;
        }
        Log.d(context.getClass().toString(), "keyword update save checked keywords: " + keywords);
        return prefs.edit().putString(type_key, newList).commit();
    }

    public static String getCustomKeywordList(Context context, String type_key) {
        SharedPreferences prefs = context.getSharedPreferences(CASHBACK_PREFS, 0);
        return prefs.getString(type_key, "");
    }

    public static boolean removeCustomKeyword(Context context,String s, String type_key) {
        SharedPreferences prefs = context.getSharedPreferences(CASHBACK_PREFS, 0);
        String oldList = getCustomKeywordList(context, type_key);
        String newList = "";
        if (TextUtils.isEmpty(oldList)) {
            return true;
        } else if (TextUtils.equals(oldList, s)) {
            newList = "";
        } else {
            String[] temp = oldList.split(",");
            for(int i = 0; i < temp.length; i++ ) {
                if (!TextUtils.equals(temp[i], s)) {
                    if(TextUtils.isEmpty(newList)) {
                        newList = temp[i];
                    } else {
                        newList += "," + temp[i];
                    }
                }
            }
        }
        Log.d(context.getClass().toString(), "keyword remove save checked keywords: " +  s);
        return prefs.edit().putString(type_key, newList).commit();
    }
}
