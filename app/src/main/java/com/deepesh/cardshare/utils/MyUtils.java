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

    public String format10DigitPhoneNumberForSearching(String phoneNumber) {
        String firstSenondDigit = phoneNumber.substring(0, 2);
        String thridFourthDigit = phoneNumber.substring(2, 4);
        String lastSixDigits = phoneNumber.substring(4, phoneNumber.length());

        return "%" + firstSenondDigit + "%" + thridFourthDigit + "%" + lastSixDigits + "%";
    }
}
