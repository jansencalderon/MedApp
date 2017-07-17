package jru.medapp.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mark Jansen Calderon on 2/2/2017.
 */

public class StringUtils {

    public static String toCurrency(int s) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(s);
    }

    public static String trim(String s) {
        return s.trim();
    }

    public static String putHashtags(String s) {
        List<String> myList = new ArrayList<>(Arrays.asList(s.split(",")));
        String putHashTags = "";
        for (int i = 0; i < myList.size(); i++) {
            putHashTags = putHashTags + " #" + myList.get(i);
        }
        return putHashTags;
    }

    public static String moneyFormat(int s) {
       // NumberFormat numberFormat = Nu
        return NumberFormat.getNumberInstance(Locale.US).format(s);
    }


}
