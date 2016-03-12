package com.sastagi.sqlitesample;

import com.sastagi.sqlitesample.model.CompanyItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sastagi on 3/12/16.
 */
public interface CompanyApiInterface {

    @GET("/{company}")
    Call<List<CompanyItem>> listCompanies(@Path("company") String user);

}
