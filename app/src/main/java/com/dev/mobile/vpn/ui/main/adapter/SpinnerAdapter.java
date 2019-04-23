package com.dev.mobile.vpn.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.mobile.vpn.R;
import com.dev.mobile.vpn.data.model.CountryResponse;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<CountryResponse> {

    private Context context;
    private List<CountryResponse> countries;
    private final LayoutInflater mInflater;
    private final int mResource;

    public SpinnerAdapter( Context context, List<CountryResponse> countries, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        this.countries = countries;
        this.context =  context;
    }


    @Override
    public int getCount() {
        return countries != null ? countries.size() : 0;
    }




    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        ImageView country_flag = view
                .findViewById(R.id.country_flag);

        TextView country_name = view.findViewById(R.id.country_name);

        CountryResponse mCountry = countries.get(position);


        if (mCountry != null) {
            if (mCountry.getName() != null)
                country_name.setText(mCountry.getName());
            if (mCountry.getFlag() != null)
                Glide
                        .with(context)
                        .load(mCountry.getFlag())
                        .into(country_flag);


        }
        return view;
    }


}
