package com.myweather.gjj.myweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.myweather.gjj.myweather.models.CityName;

import java.util.List;

/**
 * Created by 15018 on 2017/11/30.
 */

public class CityNameAdapter extends ArrayAdapter<CityName> {

    private int layoutResourceId;
    private Context context;

    public CityNameAdapter(@NonNull Context context, int
            resource, @NonNull List<CityName> objects) {
        super(context, resource, objects);
        this.context = context;
        layoutResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View
            convertView, @NonNull ViewGroup parent) {
        CityName cityName = getItem(position);
        View view = LayoutInflater.from(context).inflate(layoutResourceId, null);
        TextView textView = view.findViewById(R.id.city_name);
        textView.setText(cityName.getCity());
        return view;
    }
}
