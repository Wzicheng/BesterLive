package com.neusoft.besterlive.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.neusoft.besterlive.BesterApplication;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Wzich on 2017/10/29.
 */

public class ImgUtils {
    public static void load(String url, ImageView targetView) {
        Glide.with(BesterApplication.getApp().getApplicationContext())
                .load(url)
                .into(targetView);
    }

    public static void load(int resId, ImageView targetView) {
        Glide.with(BesterApplication.getApp().getApplicationContext())
                .load(resId)
                .into(targetView);
    }

    public static void loadRound(String url, ImageView targetView) {
        Glide.with(BesterApplication.getApp().getApplicationContext())
                .load(url)
                .bitmapTransform(new CropCircleTransformation(BesterApplication.getApp().getApplicationContext()))
                .into(targetView);
    }

    public static void loadRound(int resId, ImageView targetView) {
        Glide.with(BesterApplication.getApp().getApplicationContext())
                .load(resId)
                .bitmapTransform(new CropCircleTransformation(BesterApplication.getApp().getApplicationContext()))
                .into(targetView);
    }
}