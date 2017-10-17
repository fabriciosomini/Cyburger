package com.cynerds.cyburger.models.views;

import android.view.View;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 05/07/2017.
 */

public class CardModel {


    private String title;
    private String content;
    private String subContent;
    private int headerIconId = R.drawable.ic_closeicon;
    private int actionIconId;
    private View.OnClickListener onPictureClickListener;
    private View.OnClickListener onManageClickListener;
    private View.OnClickListener onCardViewClickListener;
    private Object extra;
    private int titleColor;

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    public View.OnClickListener getOnPictureClickListener() {
        return onPictureClickListener;
    }

    public void setOnPictureClickListener(View.OnClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    public View.OnClickListener getOnCardViewClickListener() {
        return onCardViewClickListener;
    }

    public void setOnCardViewClickListener(View.OnClickListener onCardViewClickListener) {
        this.onCardViewClickListener = onCardViewClickListener;
    }

    public View.OnClickListener getOnManageClickListener() {
        return onManageClickListener;
    }

    public void setOnManageClickListener(View.OnClickListener onManageClickListener) {
        this.onManageClickListener = onManageClickListener;
    }

    public int getHeaderIconId() {
        return headerIconId;
    }

    public void setHeaderIconId(int headerIconId) {
        this.headerIconId = headerIconId;
    }

    public int getActionIconId() {
        return actionIconId;
    }

    public void setActionIconId(int actionIconId) {
        this.actionIconId = actionIconId;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }
}