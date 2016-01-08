package com.example.andreas.securebiker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Timer;

/**
 * Created by Dominic on 29.11.2015.
 * Class for creating an AlertDialogFragment
 */
public class AlarmDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Title_AlarmDialogFragment)
                .setMessage(R.string.Message_AlarmDialogFragment)
                // button for closing the AlertDialog
                .setNeutralButton(R.string.Button_AlarmDialogFragment, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(true);

        return builder.create();
    }
}

