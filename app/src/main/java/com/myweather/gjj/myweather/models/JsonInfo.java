package com.myweather.gjj.myweather.models;

/**
 * Created by 15018 on 2017/11/11.
 */

public class JsonInfo {

    private String date;
    private String message;
    private int status;
    private String city;
    private int count;
    private Data data;

    @Override
    public String toString() {
        return "JsonInfo{" +
                "date='" + date + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", city='" + city + '\'' +
                ", count=" + count +
                ", data=" + data +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
