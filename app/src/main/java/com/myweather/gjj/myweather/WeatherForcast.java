package com.myweather.gjj.myweather;

/**
 * Created by 15018 on 2017/11/8.
 */

public class WeatherForcast {
    private int sid;
    private String startDate;
    private String city;
    private String jsonStr;
    private String state;
    private String url;
    private String memo;

    public WeatherForcast(int sid, String startDate,
                          String city, String jsonStr,
                          String state, String url,
                          String memo) {
        this.sid = sid;
        this.startDate = startDate;
        this.city = city;
        this.jsonStr = jsonStr;
        this.state = state;
        this.url = url;
        this.memo = memo;
    }

    public int getSid() {
        return sid;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCity() {
        return city;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public String getState() {
        return state;
    }

    public String getUrl() {
        return url;
    }

    public String getMemo() {
        return memo;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder("[");
//        stringBuilder.append(sid + ", ");
//        stringBuilder.append(startDate + ", ");
//        stringBuilder.append(city + ", ");
//        stringBuilder.append(jsonStr + ", ");
//        stringBuilder.append(state + ", ");
//        stringBuilder.append(url + ", ");
//        stringBuilder.append(memo + "]");
//        return stringBuilder.toString();
//    }

    @Override
    public String toString() {
        return "WeatherForcast{" +
                "sid=" + sid +
                ", startDate='" + startDate + '\'' +
                ", city='" + city + '\'' +
                ", jsonStr='" + jsonStr + '\'' +
                ", state='" + state + '\'' +
                ", url='" + url + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
