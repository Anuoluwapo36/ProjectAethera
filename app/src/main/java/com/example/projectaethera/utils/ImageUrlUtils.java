package com.example.projectaethera.utils;







import com.example.projectaethera.config.AppConfig;







/**

 * Builds full image URLs from short server paths stored in the database.

 * Use this before loading pictures with Glide or similar libraries when the API

 * returns a relative path instead of a complete http address.

 */

public final class ImageUrlUtils {







    private ImageUrlUtils() {



    }







    /**

     * Combines a relative media path with the app's configured media base URL.

     * Also accepts paths that already contain a host and strips them down first.

     *

     * @param path relative or partial URL from the server; may be null or blank

     * @return the full URL ready for image loading, or null when path is empty or unusable

     */

    public static String toFullUrl(String path) {



        if (path == null || path.trim().isEmpty()) {



            return null;



        }







        String mediaBase = AppConfig.getMediaBaseUrl();



        if (!mediaBase.endsWith("/")) {



            mediaBase = mediaBase + "/";



        }







        String trimmed = path.trim().replace('\\', '/');







        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {



            int schemeEnd = trimmed.indexOf("://") + 3;



            int pathStart = trimmed.indexOf('/', schemeEnd);



            if (pathStart < 0) {



                return null;



            }



            trimmed = trimmed.substring(pathStart + 1);



        }







        while (trimmed.startsWith("/")) {



            trimmed = trimmed.substring(1);



        }







        if (trimmed.isEmpty()) {



            return null;



        }







        return mediaBase + trimmed;



    }



}



