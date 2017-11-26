package com.myweather.gjj.myweather;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.myweather.gjj.myweather.models.DayWeatherInfo;
import com.myweather.gjj.myweather.models.JsonInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private ContentResolver contentResolver = null;
    private final Uri uri = Uri.parse("content://weather_forcast/weather_forcast");

    private Button refreshButton = null;
    private Button clearButton = null;

    private TextView cityValue = null;
    private TextView dateValue = null;
    private TextView temperatureValue = null;
    private TextView humidityValue = null;
    private TextView pm25Value = null;
    private TextView pm10Value = null;
    private TextView airQualityValue = null;
    private TextView suggestValue = null;
    private LineChart temperatureChart = null;

    private String city = "广州";

    private class RefreshTask extends AsyncTask<String, String, JsonInfo> {
        @Override
        protected JsonInfo doInBackground(String... strings) {
            JsonInfo jsonInfo = null;
            try {
                String jsonStr = Util.getAPI(strings[0]);
                Log.d(TAG, String.format("request get: %s", jsonStr));
                jsonInfo = Util.parseStr(jsonStr);
                Log.d(TAG, String.format("parse string: %s", jsonInfo.toString()));
                ContentValues values = new ContentValues();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String today = df.format(new Date());
                values.put(Settings.START_DATE, today);
                values.put(Settings.CITY, city);
                values.put(Settings.JSON, jsonStr);
                values.put(Settings.STATE, "200");
                values.put(Settings.URL, strings[0]);
                values.put(Settings.MEMO, "");
                Log.d(TAG, "insert this weather information");
                contentResolver.insert(uri, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonInfo;
        }

        @Override
        protected void onPostExecute(JsonInfo jsonInfo) {
            updateDisplayData(jsonInfo);
        }
    }

    private RefreshTask refreshTask = null;

    private String dateFormatter(String dateStr) {
        String year = dateStr.substring(0, 4);
        String month = dateStr.substring(4, 6);
        String day = dateStr.substring(6, 8);
        return year + "年" + month + "月" + day + "日";
    }

    private void doRefresh() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String today = df.format(new Date());
        String columns[] = new String[] { Settings.SID, Settings.START_DATE, Settings.CITY, Settings.JSON, Settings.STATE, Settings.URL, Settings.MEMO};
        Cursor cursor = contentResolver.query(uri, columns, "city=? and start_date=?", new String[]{city, today},null);
        boolean cached = false;
        if (cursor.moveToFirst()) {
            Log.d(TAG, String.format("%s %s weather information cached!", city, today));
            cached = true;
            String jsonStr = cursor.getString(cursor.getColumnIndex(Settings.JSON));
            JsonInfo jsonInfo = Util.parseStr(jsonStr);
            Log.d(TAG, java.lang.String.format("parse string: %s", jsonInfo.toString()));
            if (jsonInfo.getDate() == null) {
                contentResolver.delete(uri, "city=? and start_date=?", new String[]{city, today});
                Toast.makeText(this, "Invalid data, delete it! Refresh later.", Toast.LENGTH_LONG);
            } else {
                updateDisplayData(jsonInfo);
                Toast.makeText(this, "数据刷新", Toast.LENGTH_LONG);
            }
        }
        if (cached == false) {
            refreshTask = new RefreshTask();
//            refreshTask.execute("http://192.168.123.172/get_weather");
            refreshTask.execute("http://www.sojson.com/open/api/weather/json.shtml?city=" + city);
        }
    }

    private void updateDisplayData(JsonInfo jsonInfo) {
        cityValue.setText(jsonInfo.getCity());
        dateValue.setText(dateFormatter(jsonInfo.getDate()));
        temperatureValue.setText(jsonInfo.getData().getWendu());
        humidityValue.setText(jsonInfo.getData().getShidu());
        pm25Value.setText(jsonInfo.getData().getPm25() + "");
        pm10Value.setText(jsonInfo.getData().getPm10() + "");
        airQualityValue.setText(jsonInfo.getData().getQuality());
        suggestValue.setText(jsonInfo.getData().getGanmao());
        updateChatData(jsonInfo);
    }

    private void updateChatData(JsonInfo jsonInfo) {
        List<Entry> highEntries = new ArrayList<>();
        List<Entry> lowEntries = new ArrayList<>();
        int count = 1;
        float maxTemp = 0f;
        float minTemp = 50f;
        float dayHighTemp = Float.valueOf(extractTemperature(jsonInfo.getData().getYesterday().getHigh()));
        float dayLowTemp = Float.valueOf(extractTemperature(jsonInfo.getData().getYesterday().getLow()));
        highEntries.add(new Entry(count, dayHighTemp));
        lowEntries.add(new Entry(count, dayLowTemp));
        maxTemp = maxTemp > dayHighTemp ? maxTemp : dayHighTemp;
        minTemp = minTemp < dayLowTemp ? minTemp : dayLowTemp;
        for (DayWeatherInfo dayWeatherInfo : jsonInfo.getData().getForecast()) {
            count++;
            dayHighTemp = Float.valueOf(extractTemperature(dayWeatherInfo.getHigh()));
            dayLowTemp = Float.valueOf(extractTemperature(dayWeatherInfo.getLow()));
            highEntries.add(new Entry(count, dayHighTemp));
            lowEntries.add(new Entry(count, dayLowTemp));
            maxTemp = maxTemp > dayHighTemp ? maxTemp : dayHighTemp;
            minTemp = minTemp < dayLowTemp ? minTemp : dayLowTemp;
        }
        LineDataSet highDataSet = new LineDataSet(highEntries, "最高温度");
        LineDataSet lowDataSet = new LineDataSet(lowEntries, "最低温度");
        highDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lowDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        highDataSet.setColor(Color.RED);
        lowDataSet.setColor(Color.BLUE);
        highDataSet.setValueTextSize(14f);
        lowDataSet.setValueTextSize(14f);
        LineData lineData = new LineData();
        lineData.addDataSet(highDataSet);
        lineData.addDataSet(lowDataSet);
        temperatureChart.setData(lineData);
        XAxis xAxis = temperatureChart.getXAxis();
        xAxis.setAxisMaximum(count + 1);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new DateXAxisValueFormatter());
        YAxis yLAxis = temperatureChart.getAxisLeft();
        yLAxis.setAxisMaximum(maxTemp + 1);
        yLAxis.setAxisMinimum(minTemp - 1);
        YAxis yRAxis = temperatureChart.getAxisRight();
        yRAxis.setEnabled(false);

        Legend legend = temperatureChart.getLegend();
        legend.setTextSize(14f);
        temperatureChart.invalidate();
    }

    private String extractTemperature(String tempStr) {
        return tempStr.substring(3, 7);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentResolver = getContentResolver();

        setContentView(R.layout.activity_main);

        cityValue = findViewById(R.id.city_value);
        dateValue = findViewById(R.id.date_value);
        temperatureValue = findViewById(R.id.temperature_value);
        humidityValue = findViewById(R.id.humidity_value);
        pm25Value = findViewById(R.id.pm25_value);
        pm10Value = findViewById(R.id.pm10_value);
        airQualityValue = findViewById(R.id.air_quality_value);
        suggestValue = findViewById(R.id.suggest_value);
        temperatureChart = findViewById(R.id.temperature_chart);

        clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "clear button clicked!");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String today = df.format(new Date());
                contentResolver.delete(uri, "city=? and start_date=?", new String[]{city, today});
                Log.i(TAG, String.format("clear info of city: %s, date: %s", city, today));
            }
        });

        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "refresh button clicked!");
                doRefresh();
            }
        });

        doRefresh();

    }

    private class DateXAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            switch ((int) value) {
                case 1: return "昨日";
                case 2: return "今天";
                case 3: return "明日";
                case 4:
                case 5:
                case 6:
                    Date today = new Date();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(today);
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + (int) value - 2);
                    Date target = calendar.getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                    String dateStr = df.format(target);
                    int month = Integer.valueOf(dateStr.substring(5, 7));
                    int day = Integer.valueOf(dateStr.substring(8, 10));
                    return month + "月" + day +"日";
                default:
                    return "";
            }
        }
    }
}