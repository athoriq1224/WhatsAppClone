package com.example.whatsappclone.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.whatsappclone.R
import de.hdodenhof.circleimageview.CircleImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

fun populateImage(
    context: Context?,
    uri: String?,
    imageView: ImageView,
    errorDrawable: Int = R.drawable.empty
) {
    if (context != null){
        val options = RequestOptions().placeholder(progressDrawable(context)).error(errorDrawable)
        Glide.with(context).load(uri).apply(options).into(imageView)
    }

}

fun progressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 5f //ketebalan garis lingkaran
        centerRadius = 30f //diameter lingkaran
        start()
    }

}