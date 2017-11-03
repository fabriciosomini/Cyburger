package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.cynerds.cyburger.components.MessageToast;
import com.cynerds.cyburger.models.general.MessageType;

/**
 * Created by fabri on 25/10/2017.
 */

public class MessageHelper {



    public static void show(Context context, MessageType messageType, String message) {

        MessageToast messageToast = new MessageToast(context);
        messageToast.setType(messageType);
        messageToast.setText(message);

        final Toast toast  = new Toast(context);
        toast.setView(messageToast);


        LogHelper.error("Show MessageToast:" + String.valueOf(toast.getDuration()));


        new CountDownTimer(10000, 1000) {
            @Override
            public void onFinish() {
                toast.show();
            }

            @Override
            public void onTick(long millisUntilFinished) {

                toast.show();
            }
        }.start();
    }




}
