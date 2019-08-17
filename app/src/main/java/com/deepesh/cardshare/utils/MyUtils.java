package com.deepesh.cardshare.utils;

public class MyUtils {

    public String replaceSpacesAndDash(String text) {
        return text.replace(" ", "").replace("-", "");
    }

    public String getLast10Letters(String text) {
        if (text.length() >= 10)
            return text.substring(text.length() - 10);
        else
            return text;
    }
}
