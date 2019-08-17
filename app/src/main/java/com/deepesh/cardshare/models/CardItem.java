package com.deepesh.cardshare.models;

public class CardItem{
    private String person1, person2, conjunction, guestList, message;
    private int person1x;
    private int person1y;
    private int person2x;
    private int person2y;
    private int conjX;
    private int conjY;
    private int messageX;
    private int messageY;

    public CardItem(String person1, int person1x, int person1y, String person2, int person2x, int person2y,
                    String conjunction, int conjX, int conjY,  String message,  int messageX, int messageY, String guestList) {
        this.person1 = person1;
        this.person2 = person2;
        this.conjunction = conjunction;
        this.guestList = guestList;
        this.message = message;
        this.person1x = person1x;
        this.person1y = person1y;
        this.person2x = person2x;
        this.person2y = person2y;
        this.conjX = conjX;
        this.conjY = conjY;
        this.messageX = messageX;
        this.messageY = messageY;
    }

    public CardItem(String person1, int person1x, int person1y, String person2, int person2x, int person2y, String conjunction, int conjX, int conjY, String guestList) {
        this.person1 = person1;
        this.person2 = person2;
        this.conjunction = conjunction;
        this.guestList = guestList;
        this.person1x = person1x;
        this.person1y = person1y;
        this.person2x = person2x;
        this.person2y = person2y;
        this.conjX = conjX;
        this.conjY = conjY;
    }

    public String getPerson1() {
        return person1;
    }

    public void setPerson1(String person1) {
        this.person1 = person1;
    }

    public String getPerson2() {
        return person2;
    }

    public void setPerson2(String person2) {
        this.person2 = person2;
    }

    public String getConjunction() {
        return conjunction;
    }

    public void setConjunction(String conjunction) {
        this.conjunction = conjunction;
    }

    public String getGuestList() {
        return guestList;
    }

    public void setGuestList(String guestList) {
        this.guestList = guestList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPerson1x() {
        return person1x;
    }

    public void setPerson1x(int person1x) {
        this.person1x = person1x;
    }

    public int getPerson1y() {
        return person1y;
    }

    public void setPerson1y(int person1y) {
        this.person1y = person1y;
    }

    public int getPerson2x() {
        return person2x;
    }

    public void setPerson2x(int person2x) {
        this.person2x = person2x;
    }

    public int getPerson2y() {
        return person2y;
    }

    public void setPerson2y(int person2y) {
        this.person2y = person2y;
    }

    public int getConjX() {
        return conjX;
    }

    public void setConjX(int conjX) {
        this.conjX = conjX;
    }

    public int getConjY() {
        return conjY;
    }

    public void setConjY(int conjY) {
        this.conjY = conjY;
    }

    public int getMessageX() {
        return messageX;
    }

    public void setMessageX(int messageX) {
        this.messageX = messageX;
    }

    public int getMessageY() {
        return messageY;
    }

    public void setMessageY(int messageY) {
        this.messageY = messageY;
    }
}
