package com.fortunekidew.pewaad.helpers.images;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import java.io.File;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PewaaImageLoader {
    public static void loadCircleImage(Context mContext, String file, ImageView imageView, int placeHolder, int dimens) {

        Glide.with(mContext.getApplicationContext())
                .load(file)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(mContext))
                .placeholder(placeHolder)
                .error(placeHolder)
                .override(dimens, dimens)
                .into(imageView);
    }

    public static void loadCircleImage(Context mContext, int file, ImageView imageView, int placeHolder, int dimens) {
        Glide.with(mContext.getApplicationContext())
                .load(file)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(mContext))
                .placeholder(placeHolder)
                .error(placeHolder)
                .override(dimens, dimens)
                .into(imageView);
    }


    public static void loadCircleImage(Context mContext, String ImageUrl, Target target, int placeHolder, int dimens) {
        Glide.with(mContext.getApplicationContext())
                .load(ImageUrl)
                .asBitmap()
                .centerCrop()
                .transform(new CropCircleTransformation(mContext))
                .placeholder(placeHolder)
                .error(placeHolder)
                .override(dimens, dimens)
                .into(target);
    }

    public static void loadSimpleImage(Context mContext, String ImageUrl, Target target, int dimens) {

        Glide.with(mContext.getApplicationContext())
                .load(ImageUrl)
                .asBitmap()
                .centerCrop()
                .override(dimens, dimens)
                .into(target);
    }

    public static void loadSimpleImage(Context mContext, File ImageUrl, ImageView imageView, int dimens) {
        Glide.with(mContext.getApplicationContext())
                .load(ImageUrl)
                .asBitmap()
                .centerCrop()
                .override(dimens, dimens)
                .into(imageView);
    }


    public static void loadSimpleImage(Context mContext, String ImageUrl, Target target, Drawable placeHolder, int dimens) {

        Glide.with(mContext.getApplicationContext())
                .load(ImageUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(placeHolder)
                .error(placeHolder)
                .override(dimens, dimens)
                .into(target);
    }
}
