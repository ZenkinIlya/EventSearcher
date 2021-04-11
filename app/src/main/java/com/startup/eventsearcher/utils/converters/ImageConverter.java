package com.startup.eventsearcher.utils.converters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageConverter {

    public static byte[] compressImage(Context context, Uri uri){
        Bitmap bmp = null;
        byte[] data = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            data = baos.toByteArray();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
