
package com.acme.international.trading.cashbacktracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;


import com.acme.international.trading.cashbacktracker.database.AAProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CbUtils {

    public static final String INTENT_PROFILE_ID = "Item_ID";

    public static final String INTENT_PRODUCT_NAME = "product_name";

    public static final String UNSORT = "unsort";

    public static final double RATE_BV = 0.15;

    public static final double RATE_AMAZON_REF_HEALTHY = 0.15;

    public static final String EXPAND_ADAPTER_ORDER = "expand_adapter_order";

    public static final String EXPAND_ADAPTER_FBA = "expand_adapter_fba";

    public static final String INTENT_EXTRA_ITEM_STYLE = "intent_extra_item_style";

    public static final int TIME = 30;

    public static final String CASHBACK_PREFS = "cashback_prefs";
    public static final String CASHBACK_KEYWORDS_LIST_WEBSITE = "cashback_keywords_list_website";
    public static final String CASHBACK_KEYWORDS_LIST_STORE = "cashback_keywords_list_store";
    public static final String CASHBACK_KEYWORDS_LIST_CAT = "cashback_keywords_list_cat";

    public static final String PREFS_SPINNER_VIEW_OPTION = "prefs_spinner_view_option";
    public static final String PREFS_SPINNER_SORT_OPTION = "prefs_spinner_sort_option";

    public static final String MAIN_SPINNER_TYPE_BY_CB_STORE = "main_spinner_type_by_cb_store";
    public static final String MAIN_SPINNER_TYPE_BY_CA = "main_spinner_type_by_ca";
    public static final String MAIN_SPINNER_TYPE_BY_PAYMENT = "main_spinner_type_by_payment";


    public static void toContentValues(CashbackProfile profile, ContentValues values) {
        values.put(AAProvider.ProfileColumns.ORDER_ID, profile.getOrderId());
        values.put(AAProvider.ProfileColumns.ORDER_DATE, profile.getDate());
        values.put(AAProvider.ProfileColumns.ORDER_STORE, profile.getOrderStore());
        values.put(AAProvider.ProfileColumns.ORDER_DETAIL, profile.getOrderDetail());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_COMPANY, profile.getCashbackCompany());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_STATE, profile.getCashbackState());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_PERCENT, profile.getCashbackPercent());
        values.put(AAProvider.ProfileColumns.ORDER_CASHBACK_AMOUNT, profile.getCashbackAmount());
        values.put(AAProvider.ProfileColumns.ORDER_CATEGORY, profile.getCat());
        values.put(AAProvider.ProfileColumns.ORDER_TOTAL_COST, profile.getCost());
        values.put(AAProvider.ProfileColumns.ORDER_PRICE_CB_AVAILABLE, profile.getAvailableCost());
        values.put(AAProvider.ProfileColumns.ORDER_PAYMENT_FROM, profile.getPaymentFrom());
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
        int idxPartCost = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_PRICE_CB_AVAILABLE);
        int idxPaymentFrom = cursor.getColumnIndexOrThrow(AAProvider.ProfileColumns.ORDER_PAYMENT_FROM);

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
        profile.setAvailableCost(cursor.getString(idxPartCost));
        profile.setPaymentFrom(cursor.getString(idxPaymentFrom));
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

            ArrayList<CashbackProfile> list = sortListMap.get(indexYear).get(indexMonth-1);
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

    /**
     * Sort order profile list by Cashback Store
     *
     * @param profileList
     * @return ArrayList by key Cb Store and month for sorted list
     */
    public static synchronized ArrayList<ArrayList<ArrayList<CashbackProfile>>> sortProfileByKeyWords(String key,
            List<CashbackProfile> profileList) {
        if (profileList == null)
            return null;
        ArrayList<ArrayList<ArrayList<CashbackProfile>>> sortListMap = new ArrayList<ArrayList<ArrayList<CashbackProfile>>>();
        ArrayList<String> storeList = new ArrayList<String>();
        String keySort = UNSORT;
        String month = UNSORT;
        String day = UNSORT;
        // date structure Month/Date/Year
        for (CashbackProfile profile : profileList) {
            if (TextUtils.equals(key, CbUtils.MAIN_SPINNER_TYPE_BY_CB_STORE)) {
                keySort = profile.getCashbackCompany();
            } else if (TextUtils.equals(key, CbUtils.MAIN_SPINNER_TYPE_BY_CA)) {
                keySort = profile.getCat();
            } else if (TextUtils.equals(key, CbUtils.MAIN_SPINNER_TYPE_BY_PAYMENT)) {
                keySort = profile.getPaymentFrom();
            } else {
                return null;
            }
            if(TextUtils.isEmpty(keySort)) {
                keySort = "Default";
            }
            String[] mdate = profile.getDate().split("/");
            if (mdate.length == 3) {
                month = mdate[0];
                day = mdate[1];
            } else {
                return null;
            }
            int indexStoreList = -1;
            int indexMonth = Integer.parseInt(month);
            if (!storeList.contains(keySort)) {
                sortListMap.add(new ArrayList<ArrayList<CashbackProfile>>());
                storeList.add(keySort);
                indexStoreList = findStElemInArray(storeList, keySort);
                for (int i = 0; i < 12; i++) {
                    sortListMap.get(indexStoreList).add(new ArrayList<CashbackProfile>());
                }
            } else {
                indexStoreList = findStElemInArray(storeList, keySort);
            }

            ArrayList<CashbackProfile> list = sortListMap.get(indexStoreList).get(indexMonth-1);
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
        return String.valueOf(String.format("%.02f", cost));
    }

    public static String calCashbackAmount(String totalCost, String rate) {
        if (TextUtils.isEmpty(totalCost) || TextUtils.isEmpty(rate) || TextUtils.equals(".", totalCost) || TextUtils.equals(".", rate)) {
            return "0";
        }
        return String.valueOf(String.format("%.02f", Double.parseDouble(totalCost) * Double.parseDouble(rate) / 100));
    }

    public static String[] getCbStateArray(Context context) {
        return context.getResources().getStringArray(R.array.list_of_status);
    }

    public static boolean saveSpinnerPrefs(Context context, int keywords, String type_key) {
        SharedPreferences prefs = context.getSharedPreferences(CASHBACK_PREFS, 0);
        return prefs.edit().putInt(type_key, keywords).commit();
    }

    public static int getSpinnerPrefs(Context context, String type_key) {
        SharedPreferences prefs = context.getSharedPreferences(CASHBACK_PREFS, 0);
        return prefs.getInt(type_key, 0);
    }

    public static boolean saveCustomKeyword(Context context, String keywords, String type_key) {
        SharedPreferences prefs = context.getSharedPreferences(CASHBACK_PREFS, 0);
        String newList = getCustomKeywordList(context, type_key);
        if (TextUtils.isEmpty(newList)) {
            newList = keywords;
        } else {
            if(!newList.contains(keywords)) {
                newList = newList + "," + keywords;
                Log.d(context.getClass().toString(), "keyword update save checked keywords: " + keywords);
            }
        }
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

    public static boolean isLess100(String value) {
        if (TextUtils.isEmpty(value) || TextUtils.equals(".", value)) {
            return false;
        }
        Double intValue = Double.parseDouble(value);
        if (intValue < 0)
            return false;
        if (intValue <= 100) {
            return true;
        }
        return false;
    }

    public static String removeMark(String s, String mark) {
        return s.replace(mark, "");
    }

    public static String totalCashBack(Context context) {
        String total = null;
        Double total_db = 0.0;
        ArrayList<CashbackProfile> list = (ArrayList<CashbackProfile>) CbManager.getManager().getDB().getAllProfile(context.getContentResolver());
        if(list != null) {
            for(CashbackProfile p : list) {
                if (TextUtils.equals(p.getCashbackState(), context.getResources().getStringArray(R.array.list_of_status)[2]) ||
                        TextUtils.equals(p.getCashbackState(), context.getResources().getStringArray(R.array.list_of_status)[3]))
                    total_db += Double.parseDouble(p.getCashbackAmount());
            }
        }
        total = String.valueOf(String.format("%.02f", total_db));
        return total;
    }

    public static boolean isOver30Days(String sDate) {
        String[] mdate = sDate.split("/");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(mdate[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(mdate[0]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mdate[1]));
        Date date = cal.getTime();
        long millisecond = date.getTime();
        long current = System.currentTimeMillis();
        if (current - millisecond > TIME * 24 * 60 * 60000) {
            return true;
        }
        return false;
    }

    public static ArrayList<CashbackProfile> getListOfUnpaidCbProfile(Context context) {
        ArrayList<CashbackProfile> list = new ArrayList<>();
        ArrayList<CashbackProfile> fullList = (ArrayList<CashbackProfile>) CbManager.getManager().getDB().getAllProfile(context.getContentResolver());
        for(CashbackProfile p : fullList) {
            if(isOver30Days(p.getDate()) && TextUtils.equals(p.getCashbackState(), context.getResources().getStringArray(R.array.list_of_status)[1])) {
                list.add(p);
            }
        }
        return list;
    }

    public static boolean isValidDateFormate(String date, Context context) {
        String[] dates = date.split("/");
        if(dates.length != 3) {
            return false;
        }
        if(dates[0].length() != 2 && dates[1].length() != 2 && dates[2].length() != 4) {
            return false;
        }
        String[] mm = context.getResources().getStringArray(R.array.mm);
        if (!isContent(mm,dates[0])) {
            return false;
        }
        String[] day = context.getResources().getStringArray(R.array.dd);
        if (!isContent(day,dates[1])) {
            return false;
        }
        String[] year = context.getResources().getStringArray(R.array.year);
        if (!isContent(year,dates[2])) {
            return false;
        }
        return true;
    }

    public static boolean isContent(String[] array, String s) {
        boolean temp = false;
        for(int i = 0; i < array.length; i++) {
            if(TextUtils.equals(array[i],s)){
                temp = true;
                break;
            }
        }
        return temp;
    }

    public static String keyMatchCbStatus(String rawKey, Context context) {
        if (rawKey.contains(context.getResources().getStringArray(R.array.list_of_status)[1])) {
            return context.getResources().getStringArray(R.array.list_of_status)[1];
        } else if (rawKey.contains(context.getResources().getStringArray(R.array.list_of_status)[2])) {
            return context.getResources().getStringArray(R.array.list_of_status)[2];
        } else if (rawKey.contains(context.getResources().getStringArray(R.array.list_of_status)[3])) {
            return context.getResources().getStringArray(R.array.list_of_status)[3];
        }
        return null;
    }

    public static String keyMatchSort(String rawKey, Context context) {
        if (TextUtils.equals(rawKey, context.getResources().getStringArray(R.array.list_of_view_sorter)[1])) {
            return CbUtils.MAIN_SPINNER_TYPE_BY_CB_STORE;
        } else if (TextUtils.equals(rawKey, context.getResources().getStringArray(R.array.list_of_view_sorter)[2])) {
            return CbUtils.MAIN_SPINNER_TYPE_BY_CA;
        } else if (TextUtils.equals(rawKey, context.getResources().getStringArray(R.array.list_of_view_sorter)[3])) {
            return CbUtils.MAIN_SPINNER_TYPE_BY_PAYMENT;
        }
        return null;
    }
}
