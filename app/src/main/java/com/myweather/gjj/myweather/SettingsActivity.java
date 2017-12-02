package com.myweather.gjj.myweather;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myweather.gjj.myweather.models.CityName;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private ContentResolver contentResolver = null;

    private final Uri uri = Uri.parse("content://app_settings/app_settings");

    private ListView cityListView;
    private String[] cities = {"广州", "北京", "上海", "深圳"};

    private TextView selectedCityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        contentResolver = getContentResolver();

        selectedCityTextView = findViewById(R.id.selected_city);

        final List<CityName> cityList = new ArrayList<>();
        for (String city : cities)
            cityList.add(new CityName(city));

        cityListView = findViewById(R.id.city_list);

        CityNameAdapter cityNameAdapter = new CityNameAdapter(this, R.layout.city_list_layout, cityList);
        cityListView.setAdapter(cityNameAdapter);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CityName cityName = cityList.get(i);
                Log.d(TAG, cityName.getCity() + "clicked!");
                ContentValues values = new ContentValues();
                values.put(StaticValues.KEY, "city");
                values.put(StaticValues.VALUE, cityName.getCity());
                int affected = contentResolver.update(uri, values, "key=?", new String[] {"city"});
                readFromSettingsDBUpdateSelectedCity();
                Toast.makeText(getApplicationContext(), "Select city: " + cityName, Toast.LENGTH_LONG);
                hideSettingActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start settings activity");
        readFromSettingsDBUpdateSelectedCity();
    }

    private void readFromSettingsDBUpdateSelectedCity() {
        String columns[] = new String[] {StaticValues.KEY, StaticValues.VALUE};
        Cursor cursor = contentResolver.query(uri, columns, "key=?", new String[]{"city"},null);
        if (cursor.moveToFirst()) {
            String previousSelectedCity = cursor.getString(cursor.getColumnIndex(StaticValues.VALUE));
            Log.d(TAG, "previous selected city: " + previousSelectedCity);
            selectedCityTextView.setText(previousSelectedCity);
        } else {
            Log.d(TAG, "no cached previous selected city! insert default city 广州.");
            ContentValues values = new ContentValues();
            values.put(StaticValues.KEY, "city");
            values.put(StaticValues.VALUE, "广州");
            contentResolver.insert(uri, values);
        }
    }

    private void hideSettingActivity() {
        this.finish();
    }
}
