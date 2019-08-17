package com.deepesh.cardshare.models;

public class CardItem{
    private String text1, text2, text3, sharedWith;

    public CardItem(String text1, int text1x, int text1y, String text2, int text2x, int text2y, String text3, int text3x, int text3y, String sharedWith) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.sharedWith = sharedWith;
        this.text1x = text1x;
        this.text1y = text1y;
        this.text2x = text2x;
        this.text2y = text2y;
        this.text3x = text3x;
        this.text3y = text3y;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getGuestList() {
        return sharedWith;
    }

    public void setSharedWith(String sharedWith) {
        this.sharedWith = sharedWith;
    }

    public int getText1x() {
        return text1x;
    }

    public void setText1x(int text1x) {
        this.text1x = text1x;
    }

    public int getText1y() {
        return text1y;
    }

    public void setText1y(int text1y) {
        this.text1y = text1y;
    }

    public int getText2x() {
        return text2x;
    }

    public void setText2x(int text2x) {
        this.text2x = text2x;
    }

    public int getText2y() {
        return text2y;
    }

    public void setText2y(int text2y) {
        this.text2y = text2y;
    }

    public int getText3x() {
        return text3x;
    }

    public void setText3x(int text3x) {
        this.text3x = text3x;
    }

    public int getText3y() {
        return text3y;
    }

    public void setText3y(int text3y) {
        this.text3y = text3y;
    }

    private int text1x, text1y, text2x, text2y, text3x, text3y;

}
