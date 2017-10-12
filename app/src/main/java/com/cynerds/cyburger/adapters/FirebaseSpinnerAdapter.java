package com.cynerds.cyburger.adapters;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cynerds.cyburger.models.SpinnerModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

/**
 * Created by fabri on 08/07/2017.
 */

public class FirebaseSpinnerAdapter<T> extends FirebaseListAdapter<T> {


    public FirebaseSpinnerAdapter(Activity activity, Class<T> modelClass, @LayoutRes int modelLayout, Query query) {

        super(activity, modelClass, modelLayout, query);
    }


    View get(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        final T object = getItem(position);


        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

        }
        TextView make = (TextView) convertView;

        String description;
        if (object != null) {
            if (object.getClass().getSuperclass() == SpinnerModel.class) {


                description = ((SpinnerModel) object).getDescription();

            } else {

                description = "Exception: " + this.getClass().getSimpleName()
                        + " works only with " + SpinnerModel.class.getSimpleName();
            }

        } else {
            description = "";
        }

        make.setText(description);

        return convertView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        return get(position, convertView, parent);
    }

    @Override
    protected void populateView(View v, T model, int position) {


    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return get(position, convertView, parent);
    }
}