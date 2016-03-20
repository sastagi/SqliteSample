package com.sastagi.sqlitesample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sastagi.sqlitesample.SyncAdapter.Authenticator;

/**
 * Created by sastagi on 3/20/16.
 */
public class AuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}