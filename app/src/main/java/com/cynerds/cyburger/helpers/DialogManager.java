package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 08/07/2017.
 */

public class DialogManager {

    public static DialogResult Result;
    private final Context context;
    private DialogType dialogType;
    private DialogAction dialogAction;
    private AlertDialog alertDialog;
    private int layoutResId = -1;
    private View contentView;
    private AlertDialog.Builder builder;

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    private boolean centered;

    public DialogManager(Context context, DialogType dialogType) {

        this.context = context;
        this.dialogType = dialogType;
        this.dialogAction = dialogAction;

        builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();

    }


    public DialogManager(Context context) {

        this.context = context;

        builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();

    }

    public void setOnCanceListener(DialogInterface.OnCancelListener onCanceListener) {

        if (onCanceListener != null) {

            alertDialog.setOnCancelListener(onCanceListener);

        }

    }

    public void showDialog(String message) {

        showDialog("", message);
    }

    public View getContentView() {


        return alertDialog.findViewById(contentView.getId());
    }

    public void setContentView(@LayoutRes int layoutResID) {
        this.layoutResId = layoutResID;
    }

    public void showDialog(String title, String message) {


        final Context dialogContext = context;

        DialogInterface.OnClickListener dialogClickListener;
        if (dialogAction != null) {
            dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (dialogAction.getPositiveAction() != null) {
                            dialogAction.getPositiveAction().onClick(((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE));
                            break;
                        }

                    case DialogInterface.BUTTON_NEGATIVE:
                        if (dialogAction.getNegativeAction() != null) {
                            dialogAction.getNegativeAction().onClick(((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE));
                            break;
                        }

                    case DialogInterface.BUTTON_NEUTRAL:
                        if (dialogAction.getNeutralAction() != null) {
                            dialogAction.getNeutralAction().onClick(((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL));
                            break;
                        }

                }
            }
        };
        } else {
            //Se você não definir as ações do click, o comportamento será o padrão
            dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };

        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        title = title == null ? "" : title;
        message = message == null ? "" : message;

        View titleView = null;
        TextView titleText = null;
        if(centered)
        {
            titleView = inflater.inflate(R.layout.alert_dialog_title_centered, null);
            titleText = titleView.findViewById(R.id.alertTitleTextCentered);
        }else
        {
            titleView = inflater.inflate(R.layout.alert_dialog_title, null);
            titleText = titleView.findViewById(R.id.alertTitleText);
        }


        titleText.setText(title);
        alertDialog.setCustomTitle(titleView);

        if (layoutResId > -1) {

            contentView = inflater.inflate(layoutResId, null);
            if(contentView.getId()< 0)
            {
                contentView.setId(R.id.dialog_content);
            }
            contentView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    alertDialog.cancel();
                }
            });
            alertDialog.setView(contentView);

        } else {

            alertDialog.setMessage(message);

        }


        if (dialogType != null) {


            switch (dialogType) {

                case OK:
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", dialogClickListener);
                    break;

                case OK_CANCEL:
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", dialogClickListener);
                    break;

                case YES_NO:

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", dialogClickListener);
                    break;


                case SAVE_CANCEL:

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Salvar", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", dialogClickListener);
                    break;


            }
        }

       
        alertDialog.show();

        Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button positiveBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        Button neutralBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if(negativeBtn!=null){
            negativeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if(positiveBtn !=null){
            positiveBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if(neutralBtn!=null){

            neutralBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }


    }

    public void closeDialog() {

        if (alertDialog != null) {

            alertDialog.cancel();

        }
    }

    public void setAction(DialogAction action) {
        this.dialogAction = action;
    }

    public enum DialogType {
        YES_NO, OK, OK_CANCEL, SAVE_NO_CANCEL, SAVE_CANCEL
    }

    public enum DialogResult {
        YES, NO, CANCEL
    }
}
