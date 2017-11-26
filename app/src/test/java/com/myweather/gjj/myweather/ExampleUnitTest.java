package com.myweather.gjj.myweather;

import com.google.gson.Gson;
import com.myweather.gjj.myweather.models.JsonInfo;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the
 * development machine (host).
 *
 * @see
 * <a href="http://d.android.com/tools/testing">Testing
 * documentation</a>
 */
public class ExampleUnitTest {

    public static String TAG = "UnitTest";

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testNW() {
        try {
            String result = Util.getAPI("http://192.168.123.172/get_weather");
            System.out.println(String.format("get result: %s", result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExtractStr() {
        String sample = "高温 19.0℃";
        System.out.println(String.format("string length: %d", sample.length()));
        System.out.println(String.format("extracted temperature string: '%s'", sample.substring(3, 7)));
    }

    @Test
    public void testJsonParse() {
        String jsonStr = "{\"date\":\"20171111\"," +
                "\"message\":\"Success !\"," +
                "\"status\":200,\"city\":\"广州\"," +
                "\"count\":2,\"data\":{\"shidu\":\"87%\"," +
                "\"pm25\":68.0,\"pm10\":122.0," +
                "\"quality\":\"良\",\"wendu\":\"22\"," +
                "\"ganmao\":\"极少数敏感人群应减少户外活动\"," +
                "\"yesterday\":{\"date\":\"10日星期五\"," +
                "\"sunrise\":\"06:36\",\"high\":\"高温 " +
                "28.0℃\",\"low\":\"低温 19.0℃\"," +
                "\"sunset\":\"17:44\",\"aqi\":94.0," +
                "\"fx\":\"无持续风向\",\"fl\":\"<3级\"," +
                "\"type\":\"多云\"," +
                "\"notice\":\"今日多云，骑上单车去看看世界吧\"}," +
                "\"forecast\":[{\"date\":\"11日星期六\"," +
                "\"sunrise\":\"06:37\",\"high\":\"高温 " +
                "28.0℃\",\"low\":\"低温 20.0℃\"," +
                "\"sunset\":\"17:44\",\"aqi\":96.0," +
                "\"fx\":\"无持续风向\",\"fl\":\"<3级\"," +
                "\"type\":\"多云\"," +
                "\"notice\":\"悠悠的云里有淡淡的诗\"}," +
                "{\"date\":\"12日星期日\"," +
                "\"sunrise\":\"06:38\",\"high\":\"高温 " +
                "25.0℃\",\"low\":\"低温 19.0℃\"," +
                "\"sunset\":\"17:44\",\"aqi\":83.0," +
                "\"fx\":\"无持续风向\",\"fl\":\"<3级\"," +
                "\"type\":\"小雨\"," +
                "\"notice\":\"下雨了不要紧，撑伞挡挡就行\"}," +
                "{\"date\":\"13日星期一\"," +
                "\"sunrise\":\"06:38\",\"high\":\"高温 " +
                "23.0℃\",\"low\":\"低温 18.0℃\"," +
                "\"sunset\":\"17:43\",\"aqi\":101.0," +
                "\"fx\":\"无持续风向\",\"fl\":\"<3级\"," +
                "\"type\":\"小到中雨\"," +
                "\"notice\":\"今日将有小到中雨，出门请携带雨具\"}," +
                "{\"date\":\"14日星期二\"," +
                "\"sunrise\":\"06:39\",\"high\":\"高温 " +
                "24.0℃\",\"low\":\"低温 18.0℃\"," +
                "\"sunset\":\"17:43\",\"aqi\":111.0," +
                "\"fx\":\"北风\",\"fl\":\"3-4级\"," +
                "\"type\":\"多云\"," +
                "\"notice\":\"今日多云，骑上单车去看看世界吧\"}," +
                "{\"date\":\"15日星期三\"," +
                "\"sunrise\":\"06:40\",\"high\":\"高温 " +
                "26.0℃\",\"low\":\"低温 17.0℃\"," +
                "\"sunset\":\"17:43\",\"aqi\":93.0," +
                "\"fx\":\"无持续风向\",\"fl\":\"<3级\"," +
                "\"type\":\"多云\"," +
                "\"notice\":\"今日多云，骑上单车去看看世界吧\"}]}}";
        Gson gson = new Gson();
        JsonInfo jsonInfo = gson.fromJson(jsonStr, JsonInfo.class);
        System.out.println(String.format("json info: %s", jsonInfo.toString()));
    }
}