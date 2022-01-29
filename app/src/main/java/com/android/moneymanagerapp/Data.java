package com.android.moneymanagerapp;

public class Data {
    String item,date,id,itemNday,itemNweek,itemNmonth;
    int amount,week,month;
    String comment;

    public Data() {

    }

    public Data(String item, String date, String id, String itemNday, String itemNweek, String itemNmonth, int amount, int week, int month, String comment) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.itemNday = itemNday;
        this.itemNweek = itemNweek;
        this.itemNmonth = itemNmonth;
        this.amount = amount;
        this.week = week;
        this.month = month;
        this.comment = comment;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemNday() {
        return itemNday;
    }

    public void setItemNday(String itemNday) {
        this.itemNday = itemNday;
    }

    public String getItemNweek() {
        return itemNweek;
    }

    public void setItemNweek(String itemNweek) {
        this.itemNweek = itemNweek;
    }

    public String getItemNmonth() {
        return itemNmonth;
    }

    public void setItemNmonth(String itemNmonth) {
        this.itemNmonth = itemNmonth;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
