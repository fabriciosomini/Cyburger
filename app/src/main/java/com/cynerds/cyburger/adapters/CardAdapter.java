package com.cynerds.cyburger.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.models.views.CardModel;

import java.util.List;

/**
 * Created by fabri on 05/07/2017.
 */

public class CardAdapter extends ArrayAdapter<CardModel> {


    public CardAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CardModel> objects) {
        super(context, resource, objects);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        final CardModel cardModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_card_view, parent, false);
        }
        // Lookup view for data population
        ImageView cardIcon = convertView.findViewById(R.id.cardIcon);
        TextView cardTitle = convertView.findViewById(R.id.cardTitle);
        TextView cardContent = convertView.findViewById(R.id.cardContent);
        ImageView cardManageIcon = convertView.findViewById(R.id.cardManageIcon);
        ConstraintLayout baseComponentContainer = convertView.findViewById(R.id.dashboard_cardview);
        View.OnClickListener onCardViewClickListener = cardModel.getOnCardViewClickListener();
        View.OnClickListener onManageClickListener = cardModel.getOnManageClickListener();
        View.OnClickListener onPictureViewClickListener = cardModel.getOnPictureClickListener();

        cardManageIcon.setImageResource(cardModel.getActionIconId());


        if (cardModel.getTitleColor() > 0) {
            cardTitle.setTextColor(ContextCompat.getColor(getContext(), cardModel.getTitleColor()));
        }

        if (onPictureViewClickListener != null) {
            cardIcon.setClickable(true);
            cardIcon.setOnClickListener(onPictureViewClickListener);

        }

        if(onCardViewClickListener !=null)
        {
            baseComponentContainer.setOnClickListener(onCardViewClickListener);

        }

        if(onManageClickListener !=null)
        {
            cardManageIcon.setOnClickListener(onManageClickListener);

        }

        cardIcon.setImageResource(cardModel.getHeaderIconId());
        cardTitle.setText(cardModel.getTitle());
        cardContent.setText(cardModel.getContent());

        return convertView;
    }



}