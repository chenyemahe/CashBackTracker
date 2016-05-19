package com.acme.international.trading.cashbacktracker.keywords;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.R;

import java.util.ArrayList;

/**
 * Created by ye1.chen on 3/29/16.
 */
public class KeywordsSettingsAdapter extends BaseAdapter {

    private ArrayList<String> stringList;
    private String[] baseList;
    private Context mContext;
    private String mStyle;

    public static final String CASHBACK_WEB = "cashback_web";
    public static final String STORE = "store_cashbak";

    public KeywordsSettingsAdapter(Context context, String style) {
        mContext = context;
        mStyle = style;
    }

    @Override
    public int getCount() {
        if (stringList != null) {
            return stringList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (stringList != null) {
            return stringList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_ite_keywords, null);
        }
        LanguageListViewHolder viewHolder = new LanguageListViewHolder(convertView, position);
        convertView.setTag(viewHolder);
        return convertView;
    }

    public void setData() {
        String extraList = null;
        if (TextUtils.equals(mStyle, CASHBACK_WEB)) {
            extraList = CbUtils.getCustomKeywordList(mContext, CbUtils.CASHBACK_KEYWORDS_LIST_WEBSITE);
            baseList = mContext.getResources().getStringArray(R.array.list_of_cashback_website);
        } else if (TextUtils.equals(mStyle, STORE)) {
            extraList = CbUtils.getCustomKeywordList(mContext, CbUtils.CASHBACK_KEYWORDS_LIST_STORE);
            baseList = mContext.getResources().getStringArray(R.array.list_of_store);
        }
        if (stringList == null)
            stringList = new ArrayList<String>();
        stringList.clear();
        for (int i = 0; i < baseList.length; i++) {
            stringList.add(baseList[i]);
        }
        if (!TextUtils.isEmpty(extraList)) {
            String[] temp = extraList.split(",");
            for (int i = 0; i < temp.length; i++) {
                stringList.add(temp[i]);
            }
        }
    }

    public String getStyle() {
        return mStyle;
    }

    public void setStyle(String s) {
        mStyle = s;
    }

    public void refresh() {
        setData();
        notifyDataSetChanged();
    }

    public class LanguageListViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private TextView mDelete;
        private int mPosition;

        public LanguageListViewHolder(View v, int position) {
            mPosition = position;
            mTextView = (TextView) v.findViewById(R.id.tv_keyword_body);
            SpannableString spanString = new SpannableString((CharSequence) getItem(position));
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            mTextView.setText(spanString);
            mDelete = (TextView) v.findViewById(R.id.tv_delete);
            boolean showDeleteState = true;
            for (int i = 0; i < baseList.length; i++) {
                if (TextUtils.equals(stringList.get(mPosition), baseList[i])) {
                    mDelete.setVisibility(View.GONE);
                    showDeleteState = false;
                    break;
                }
            }
            if (showDeleteState) {
                mDelete.setVisibility(View.VISIBLE);
                mDelete.setOnClickListener(null);
                mDelete.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_delete) {
                if (TextUtils.equals(mStyle, CASHBACK_WEB)) {
                    CbUtils.removeCustomKeyword(mContext, stringList.get(mPosition), CbUtils.CASHBACK_KEYWORDS_LIST_WEBSITE);
                } else if (TextUtils.equals(mStyle, STORE)) {
                    CbUtils.removeCustomKeyword(mContext, stringList.get(mPosition), CbUtils.CASHBACK_KEYWORDS_LIST_STORE);
                }
                refresh();
            }
        }
    }
}
