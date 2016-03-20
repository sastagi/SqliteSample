package com.sastagi.sqlitesample;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.sastagi.sqlitesample.Database.CompanyDatabaseHelper;
import com.sastagi.sqlitesample.Networking.FetchCompanies;
import com.sastagi.sqlitesample.model.CompanyItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CompanyAdapter mCompanyAdapter;
    private ArrayList<CompanyItem> companyItems = new ArrayList<CompanyItem>();

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.sastagi.sqlitesample.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "sqllitesample.com";
    // The account name
    public static final String ACCOUNT = "android.accounts.AccountAuthenticator";
    // Instance fields
    Account mAccount;

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;//Sync interval is in seconds not milliseconds
    ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccount = CreateSyncAccount(this);

        mResolver = getContentResolver();
        /*
         * Turn on periodic syncing
         */
        //ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        /*ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                10L);*/
        /*Bundle syncSettingsBundle = new Bundle();
        syncSettingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,
                true);
        syncSettingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,
                true);
        ContentResolver.requestSync(mAccount, AUTHORITY, syncSettingsBundle);*/

        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                60L);


        //create a service to add/update data to DB
        Intent intentService = new Intent(this, FetchCompanies.class);
        startService(intentService);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            Log.i("TEST","REC");
            final CompanyDatabaseHelper companyDatabaseHelper = CompanyDatabaseHelper.getInstance(getBaseContext());
            if (resultCode == RESULT_OK) {
                Log.i("TEST", "DATARETURNED");
                if (companyItems.size()==0){
                    companyItems = (ArrayList)companyDatabaseHelper.getAllCompanies();
                    companyItems.get(0).getTitle();
                    Log.i("TEST",companyItems.get(0).getTitle());
                }
                mCompanyAdapter = new CompanyAdapter(companyItems);
                mRecyclerView.setAdapter(mCompanyAdapter);
                //Add items to arraylist
                //do notifydatasetchanged
                //String resultValue = intent.getStringExtra("resultValue");
                //Toast.makeText(MainActivity.this, resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(FetchCompanies.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
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
