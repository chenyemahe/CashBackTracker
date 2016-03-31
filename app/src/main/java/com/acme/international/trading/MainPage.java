package com.acme.international.trading;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import com.acme.international.trading.cashbacktracker.CashBackPage;
import com.acme.international.trading.cashbacktracker.R;
import com.acme.international.trading.giftcardtracker.GiftCardPage;

/**
 * Created by ye1.chen on 3/22/16.
 */
public class MainPage extends Activity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button cashBack = (Button) findViewById(R.id.bt_cashback);
        cashBack.setOnClickListener(this);
        Button giftCard = (Button) findViewById(R.id.bt_gift_card);
        giftCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cashback:
                startActivity(new Intent(this, CashBackPage.class));
                break;
            case R.id.bt_gift_card:
                startActivity(new Intent(this, GiftCardPage.class));
                break;

        }
    }
}
