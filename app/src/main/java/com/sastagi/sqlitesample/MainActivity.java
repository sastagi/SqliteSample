package com.sastagi.sqlitesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.sastagi.sqlitesample.Database.CompanyDatabaseHelper;
import com.sastagi.sqlitesample.model.CompanyItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    for(int i=0;i<response.body().size();i++)
                        companyDatabaseHelper.addPost(response.body().get(i));

            }

            @Override
            public void onFailure(Call<List<CompanyItem>> call, Throwable t) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
