package com.nakul.wikiapi.network;


import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;


public class ImageBinder {

    @BindingAdapter({"imageURL"})
    public static void loadImage(ImageView img, String imageUrl) {
        Glide.with(img.getContext())
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(img);
    }
}
