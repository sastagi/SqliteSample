package com.sastagi.sqlitesample.Networking;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sastagi.sqlitesample.CompanyApiInterface;
import com.sastagi.sqlitesample.Database.CompanyDatabaseHelper;
import com.sastagi.sqlitesample.model.CompanyItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sastagi on 3/12/16.
 */
public class FetchCompanies extends IntentService{

    public static final String ACTION = "com.codepath.example.servicesdemo.MyTestService";

    public FetchCompanies(){
        super("FetchCompanies");
    }

    @Override
    public void onHandleIntent(Intent intent){
        final CompanyDatabaseHelper companyDatabaseHelper = CompanyDatabaseHelper.getInstance(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.4:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CompanyApiInterface companyService = retrofit.create(CompanyApiInterface.class);
        Call<List<CompanyItem>> companyItems = companyService.listCompanies("company");

        companyItems.enqueue(new Callback<List<CompanyItem>>() {
            @Override
            public void onResponse(Call<List<CompanyItem>> call, Response<List<CompanyItem>> response) {
                Log.i("TEST", response.body().get(0).getTitle());
                for (int i = 0; i < response.body().size(); i++)
                    companyDatabaseHelper.addPost(response.body().get(i));
                Intent intenting = new Intent(ACTION);
                intenting.putExtra("resultCode", Activity.RESULT_OK);


                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intenting);

            }

            @Override
            public void onFailure(Call<List<CompanyItem>> call, Throwable t) {

            }
        });
    }
}
