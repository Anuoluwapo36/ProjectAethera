package com.example.projectaethera.utils;



import android.content.Context;

import android.net.Uri;



import java.io.File;

import java.io.FileOutputStream;

import java.io.InputStream;



/**

 * File helpers for turning Android content URIs into real files on disk.

 * Use this when the app picks an image from the gallery or camera and the upload

 * code needs a {@link File} instead of a {@link Uri}.

 */

public class FileUtils {



    /**

     * Copies the content behind a URI into a temporary JPG file in the app cache.

     * Use before uploading media so network code can read bytes from a normal file path.

     *

     * @param context used to open the content resolver and access the cache directory

     * @param uri     the image URI returned by the picker or camera intent

     * @return the cached file copy, or null if reading or writing failed

     */

    public static File uriToFile(Context context, Uri uri){

        try{

            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            String uniqueName = "upload_" + System.currentTimeMillis() + ".jpg";

            File file = new File(context.getCacheDir(), uniqueName);

            FileOutputStream outputStream = new FileOutputStream(file);



            byte[] buffer = new byte[1024];

            int len;



            while ((len = inputStream.read(buffer)) != -1){

                outputStream.write(buffer,0,len);



            }

            outputStream.close();

            inputStream.close();

            return file;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

}

