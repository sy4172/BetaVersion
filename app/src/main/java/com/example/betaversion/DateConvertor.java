package com.example.betaversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * * @author    Shahar Yani
 * * @version  	1.1
 * * @since		03/03/2022
 *
 * * This DateConvertor.class concerts a String to Date OR a Date to String.
 */
public class DateConvertor {

    /**
     * Date to string method.
     *
     * @param selectedDate the selected date
     * @param strFormat    the str format to be displayed.
     * @return the string
     */
    public static String dateToString (Date selectedDate, String strFormat){
        DateFormat dateFormat = new SimpleDateFormat(strFormat, Locale.ENGLISH);
        String dateStr = dateFormat.format(selectedDate);

        return dateStr;
    }

    /**
     * String to date method.
     *
     * @param strDate   the str date
     * @param strFormat the str format to be displayed.
     * @return the date
     */
    public static Date stringToDate (String strDate, String strFormat){
        Date selectedDate = null;
        DateFormat dateFormat = new SimpleDateFormat(strFormat, Locale.ENGLISH);
        try {
            selectedDate = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return selectedDate;
    }

}
