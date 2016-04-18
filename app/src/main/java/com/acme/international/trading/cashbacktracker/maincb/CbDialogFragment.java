package com.acme.international.trading.cashbacktracker.maincb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbManager;
import com.acme.international.trading.cashbacktracker.R;

/**
 * Created by ye1.chen on 4/13/16.
 */

public class CbDialogFragment extends DialogFragment {

    private CashbackProfile profile;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getActivity().getResources().getString(R.string.dialog_body))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CbManager.getManager().getDB().deleteAAProfile(getActivity().getContentResolver(), profile);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });;
        return builder.create();
    }

    public void setProfile(CashbackProfile p) {
        profile = p;
    }
}
