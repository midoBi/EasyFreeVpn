package com.dev.mobile.vpn.ui.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dev.mobile.vpn.R;
import com.dev.mobile.vpn.data.local.CountryDao;
import com.dev.mobile.vpn.data.model.CountryResponse;
import com.dev.mobile.vpn.ui.main.adapter.SpinnerAdapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.county_list)
    SearchableSpinner mSearchableSpinner;


    SpinnerAdapter mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);



        List<CountryResponse> countries = CountryDao.getInstance().getCountries();





        mSpinnerAdapter = new SpinnerAdapter(this,countries,R.layout.country_row);

       // mSearchableSpinner.setAdapter(mSpinnerAdapter);

    }


}
