package com.fortunecookie.fortunecookie2;



        import java.util.Date;

/**
 * Created by parth on 2/17/2018.
 */



public class CustomUsageStat {
    public String app_id;
    public String app_name;
    public String ticker;
    public String date_of_usage;
    public Double hours_used;

    public CustomUsageStat(String app_id, String app_name, String ticker, String date_of_usage, Double hours){
        this.app_id = app_id;
        this.app_name = app_name;
        this.ticker = ticker;
        this.date_of_usage = date_of_usage;
        this.hours_used = hours;
    }

    @Override
    public String toString(){
        return this.ticker + " " + this.app_id + " " + this.app_name + " " + this.date_of_usage + " "
                + Double.toString(this.hours_used);
    }

}
