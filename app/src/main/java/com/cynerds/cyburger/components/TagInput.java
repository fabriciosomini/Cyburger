package com.cynerds.cyburger.components;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.models.Tag;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagInput extends ConstraintLayout {


    private final Context context;
    private List<Tag> tagList;

    public TagInput(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
        this.context = context;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_tag_input, this);
    }

    public void setFilterableList(List<Tag> tagList){

        this.tagList = tagList;
        FlexboxLayout flexboxLayout =  findViewById(R.id.tagItemsContainer);
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.removeAllViews();

        for (Tag tag :
                tagList) {
            TagItem tagInput =  new TagItem(context);
            tagInput.setText(tag.getDescription());

            TextView textView = new TextView(context);
            textView.setText(tag.getDescription() + "    ");


            flexboxLayout.addView(tagInput);

        }


    }
}