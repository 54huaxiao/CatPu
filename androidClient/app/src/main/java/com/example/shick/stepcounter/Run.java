package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/1.
 */
public class Run {
    private String date;
    private String time;
    private String distance;
    private String order;
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

    public String getOrder() {return order;}

    public Run(String d, String t, String dis, String or) {
        date = d;
        time = t;
        distance = dis;
        order = or;
    }
}
