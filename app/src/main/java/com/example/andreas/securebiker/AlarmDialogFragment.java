package com.example.andreas.securebiker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Dominic on 29.11.2015.
 * Klasse zur Erstellung eines Warn-Dialogs
 */
public class AlarmDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Title_AlarmDialogFragment)
                .setMessage(R.string.Message_AlarmDialogFragment)
                /*.setNeutralButton(R.string.Button_AlarmDialogFragment, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endNotification();
                    }
                })*/
                .setCancelable(true);

        return builder.create();
    }

    /**
     * Methode zum Abbrechen des Alarmtons, der durch Notification in GeofenceIntentService generiert wird
     * Button wird derzeit nicht verwendet, Methode daher auskommentiert
     */
   /* public void endNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }*/
}

