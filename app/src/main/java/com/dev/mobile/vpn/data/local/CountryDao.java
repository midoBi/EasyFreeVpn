package com.dev.mobile.vpn.data.local;

import com.dev.mobile.vpn.data.model.CountryResponse;
import com.dev.mobile.vpn.util.Constant;

import java.util.List;

import static io.paperdb.Paper.book;

public class CountryDao {

    private static CountryDao countryDao = null;
    public static final  String COUNTRY_KEY = "country_key";

    public CountryDao() {
    }

    public static CountryDao getInstance(){
        if (countryDao == null){
            countryDao = new CountryDao();
        }
        return  countryDao;
    }

    public List<CountryResponse> getCountries() {
        return  book(Constant.DB_NAME).read(COUNTRY_KEY,null);
    }

    public void putCountries(List<CountryResponse> mList) {
        book(Constant.DB_NAME).write(COUNTRY_KEY,mList);
    }


}
