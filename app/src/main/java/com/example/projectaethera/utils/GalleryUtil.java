package com.example.projectaethera.utils;



import android.content.ContentUris;

import android.content.Context;

import android.database.Cursor;

import android.net.Uri;

import android.provider.MediaStore;

import java.util.ArrayList;

import java.util.List;



/**

 * Reads images from the device photo gallery through MediaStore.

 * Use this when a screen needs to list local photos or grab the most recent one,

 * for example when building a custom gallery picker.

 */

public class GalleryUtil {



    /**

     * Loads every image on external storage, newest first.

     * Use when you need the full gallery list for selection grids or carousels.

     *

     * @param context needed to query the MediaStore content resolver

     * @return a list of content URIs pointing at each image; empty if none are found

     */

    public static List<Uri> getAllImages(Context context) {

        List<Uri> imagesList = new ArrayList<>();



        String[] projection = {MediaStore.Images.Media._ID};

        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;



        try (Cursor cursor = context.getContentResolver().query(

                collection,

                projection,

                null,

                null,

                MediaStore.Images.Media.DATE_ADDED + " DESC"

        )) {

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    long id = cursor.getLong(0);

                    Uri imageUri = ContentUris.withAppendedId(

                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    imagesList.add(imageUri);

                }

            }

        }

        return imagesList;

    }



    /**

     * Returns the newest image from the gallery, if any exist.

     * Handy as a quick default thumbnail without loading the whole gallery list yourself.

     *

     * @param context needed to query the MediaStore content resolver

     * @return the most recently added image URI, or null when the gallery is empty

     */

    public static Uri getFirstImage(Context context) {

        List<Uri> list = getAllImages(context);

        if (list != null && !list.isEmpty()) {

            return list.get(0);

        }

        return null;

    }

}

