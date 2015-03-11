package com.rabor.tipcalculator;

/**
 * Created by Aaron on 3/7/2015.
 */
public class Tip {
    private int id;
    private double tip_percent;
    private double bill_amount;
    private int bill_date;

    public Tip(){
        id = 0;
        tip_percent = 0.0;
        bill_amount = 0.0;
        bill_date = 0;
    }

    public Tip(int id, double percent, double amount, int date){
        this.id = id;
        tip_percent = percent;
        bill_amount = amount;
        bill_date = date;
    }

    public void setId(int value){
        id = value;
    }
    public int getId(){
        return id;
    }
    public void setTip_percent(double value){
        tip_percent = value;
    }
    public double getTip_percent(){
        return tip_percent;
    }
    public void setBill_amount(double value){
        bill_amount = value;
    }
    public double getBill_amount(){
        return bill_amount;
    }
    public void setBill_date(int value){
        bill_date = value;
    }
    public int getBill_date(){
        return bill_date;
    }
}
