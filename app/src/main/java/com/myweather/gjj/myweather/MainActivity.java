package com.myweather.gjj.myweather;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

    private Context context = this;

    private ContentResolver contentResolver = null;
    private final Uri weatherContentUri = Uri.parse("content://weather_forcast/weather_forcast");
    private final Uri settingsUri = Uri.parse("content://app_settings/app_settings");

    private Button refreshButton = null;
    private Button clearButton = null;
    private Button settingsButton = null;

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
                //这里访问网络
                String jsonStr = Util.getAPI(strings[0]);
                Log.d(TAG, String.format("request get: %s", jsonStr));
                jsonInfo = Util.parseStr(jsonStr);
                Log.d(TAG, String.format("parse string: %s", jsonInfo.toString()));
                //这里决定访问网络得到的数据是否有用，就是是否有城市信息这个值
                if (jsonInfo.getCity() == null)
                    return null;
                ContentValues values = new ContentValues();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String today = df.format(new Date());
                values.put(StaticValues.START_DATE, today);
                values.put(StaticValues.CITY, city);
                values.put(StaticValues.JSON, jsonStr);
                values.put(StaticValues.STATE, "200");
                values.put(StaticValues.URL, strings[0]);
                values.put(StaticValues.MEMO, "");
                Log.d(TAG, "insert this weather information");
                contentResolver.insert(weatherContentUri, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonInfo;
        }

        @Override
        protected void onPostExecute(JsonInfo jsonInfo) {
            //访问网络结束，如果有数据则更新，否则等待下一次的网络访问
            if (jsonInfo != null)
                updateDisplayData(jsonInfo);
            else {
                Log.e(TAG, "Response json data is not correct! Do nothing!");
                Toast.makeText(context, "Incorrect Response!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private RefreshTask refreshTask = null;

    private String dateFormatter(String dateStr) {
        String year = dateStr.substring(0, 4);
        String month = dateStr.substring(4, 6);
        String day = dateStr.substring(6, 8);
        return year + "年" + month + "月" + day + "日";
    }

    /**
     * 进行刷新数据的操作
     */
    private void doRefresh() {
        //get city from settings provider
        //先从setting中获取已经设置的城市信息，如果第一次使用软件没有设置过的话默认广州
        String[] columns = new String[]{StaticValues.KEY, StaticValues.VALUE};
        Cursor cursor = contentResolver.query(settingsUri, columns, "key=?", new String[]{"city"}, null);
        if (cursor.moveToFirst()) {
            city = cursor.getString(cursor.getColumnIndex(StaticValues.VALUE));
        } else {
            Log.d(TAG, "no cached previous selected city! use default city 广州.");
            city = "广州";
        }

        //refresh
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String today = df.format(new Date());
        //需要查询出来的列信息
        columns = new String[] { StaticValues.SID, StaticValues.START_DATE, StaticValues.CITY, StaticValues.JSON, StaticValues.STATE, StaticValues.URL, StaticValues.MEMO};
        //通过查询获得数据集的游标
        cursor = contentResolver.query(weatherContentUri, columns, "city=? and start_date=?", new String[]{city, today},null);
        boolean cached = false;
        if (cursor.moveToFirst()) {
            Log.d(TAG, String.format("%s %s weather information cached!", city, today));
            cached = true;
            //将查询所得数据转换成复杂数据结构，用于后续更新数据
            String jsonStr = cursor.getString(cursor.getColumnIndex(StaticValues.JSON));
            JsonInfo jsonInfo = Util.parseStr(jsonStr);
            Log.d(TAG, java.lang.String.format("parse string: %s", jsonInfo.toString()));
            if (jsonInfo.getDate() == null) {
                contentResolver.delete(weatherContentUri, "city=? and start_date=?", new String[]{city, today});
                Toast.makeText(context, "Invalid data, delete it! Refresh later.", Toast.LENGTH_LONG).show();
            } else {
                updateDisplayData(jsonInfo);
                Toast.makeText(context, "数据已缓存", Toast.LENGTH_LONG).show();
            }
        }
        //未缓存数据的话就访问网络获取数据
        if (cached == false) {
            refreshTask = new RefreshTask();
//            refreshTask.execute("http://192.168.123.172/get_weather");
            refreshTask.execute("http://www.sojson.com/open/api/weather/json.shtml?city=" + city);
        }
    }

    /**
     * 更新显示的数据
     * @param jsonInfo 更新数据和画图所需要的数据保存的数据结构的变量
     */
    private void updateDisplayData(JsonInfo jsonInfo) {
        if (jsonInfo == null) {
            return;
        }
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

    /**
     * 更新数据表格的方法，写得比较臃肿
     * 1、先将x, y数据对保存在Entry中
     * 2、将Entry放到DataSet中，DataSet可以设置样式
     * 3、强DataSet放到LineData中，LineData可以添加多个DataSet
     * 4、chart利用setData的方法设置数据
     * 5、数据轴设置格式
     * 6、chart调用invalidate方法绘图
     * @param jsonInfo 画图需要的数据所保存的数据结构的变量
     */
    private void updateChatData(JsonInfo jsonInfo) {
        List<Entry> highEntries = new ArrayList<>();
        List<Entry> lowEntries = new ArrayList<>();
        int count = 1;
        float maxTemp = 0f;
        float minTemp = 50f;
        float dayHighTemp = Float.valueOf(extractTemperature(jsonInfo.getData().getYesterday().getHigh()));
        float dayLowTemp = Float.valueOf(extractTemperature(jsonInfo.getData().getYesterday().getLow()));
        //1、
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
        //2、
        LineDataSet highDataSet = new LineDataSet(highEntries, "最高温度");
        LineDataSet lowDataSet = new LineDataSet(lowEntries, "最低温度");
        highDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lowDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        highDataSet.setColor(Color.RED);
        lowDataSet.setColor(Color.BLUE);
        highDataSet.setValueTextSize(14f);
        lowDataSet.setValueTextSize(14f);
        //3、
        LineData lineData = new LineData();
        lineData.addDataSet(highDataSet);
        lineData.addDataSet(lowDataSet);
        //4、
        temperatureChart.setData(lineData);
        //5、
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
        //6、
        temperatureChart.invalidate();
    }

    private String extractTemperature(String tempStr) {
        String temp = tempStr.substring(3);
        return temp.substring(0, temp.length() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contentResolver = getContentResolver();

        setContentView(R.layout.activity_main);

        //这些是表格上方需要显示的值
        cityValue = findViewById(R.id.city_value);
        dateValue = findViewById(R.id.date_value);
        temperatureValue = findViewById(R.id.temperature_value);
        humidityValue = findViewById(R.id.humidity_value);
        pm25Value = findViewById(R.id.pm25_value);
        pm10Value = findViewById(R.id.pm10_value);
        airQualityValue = findViewById(R.id.air_quality_value);
        suggestValue = findViewById(R.id.suggest_value);
        temperatureChart = findViewById(R.id.temperature_chart);

        //清除按钮以及点击监听器，清除时使用查询删除相应的记录
        clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "clear button clicked!");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String today = df.format(new Date());
                contentResolver.delete(weatherContentUri, "city=? and start_date=?", new String[]{city, today});
                Log.i(TAG, String.format("clear info of city: %s, date: %s", city, today));
            }
        });

        //刷新按钮，调用刷新的方法
        refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "refresh button clicked!");
                doRefresh();
            }
        });

        settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "settings button clicked!");
                Intent intent = new Intent();
                intent.setClass(context, SettingsActivity.class);
                context.startActivity(intent);
            }
        });

    }

    /**
     * MainActivity在展示之前会调用这个方法，做一次刷新
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "On main activity start, do refresh operation.");
//        Toast.makeText(context, "On main activity start.", Toast.LENGTH_LONG).show();
        doRefresh();
    }

    /**
     * 内部类，规定表格X轴的值的格式化方式
     */
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
