package com.example.projectaethera.utils;



import java.sql.Timestamp;

import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Locale;

import java.util.TimeZone;



/**

 * Turns timestamps into short "time ago" labels for posts and comments.

 * Use this anywhere the UI should show friendly relative times like "5m ago"

 * instead of raw database or ISO date strings.

 */

public class TimeAgoUtils {



    /**

     * Formats a SQL timestamp as a compact relative time string.

     * Use when you already have a {@link Timestamp} from the database layer.

     *

     * @param timestamp the moment to describe; may be null

     * @return a label such as "3h ago", or "Unknown time" when timestamp is null

     */

    public static String format(Timestamp timestamp) {

        if (timestamp == null) return "Unknown time";



        long time = timestamp.getTime();

        long now = System.currentTimeMillis();

        long diff = now - time;



        if (diff < 0) return "Just now";



        long second = 1000;

        long minute = 60 * second;

        long hour = 60 * minute;

        long day = 24 * hour;

        long week = 7 * day;

        long month = 30 * day;

        long year = 365 * day;



        if (diff < minute) {

            return (diff / second) + "s ago";

        } else if (diff < hour) {

            return (diff / minute) + "m ago";

        } else if (diff < day) {

            return (diff / hour) + "h ago";

        } else if (diff < week) {

            return (diff / day) + "d ago";

        } else if (diff < month) {

            return (diff / week) + "w ago";

        } else if (diff < year) {

            return (diff / month) + "mo ago";

        } else {

            return (diff / year) + "y ago";

        }

    }



    /**

     * Parses an ISO-style date string from the API and formats it as time ago.

     * Use when the backend sends dates as text rather than {@link Timestamp} objects.

     *

     * @param isoDate date text from the server (supports common ISO and space-separated forms)

     * @return a relative time label, or an empty string when the input is missing or invalid

     */

    public static String formatIso(String isoDate) {

        if (isoDate == null || isoDate.isEmpty()) {

            return "";

        }

        try {

            String normalized = isoDate.replace(" ", "T");

            if (normalized.endsWith("Z")) {

                normalized = normalized.substring(0, normalized.length() - 1);

            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = sdf.parse(normalized.length() >= 19 ? normalized.substring(0, 19) : normalized);

            if (date == null) {

                return "";

            }

            return format(new Timestamp(date.getTime()));

        } catch (ParseException e) {

            return "";

        }

    }

}

