/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This class implements a service to run the account authenticator required by the sync adapter.
 */
public class SyncAuthnrSvc extends Service {

    private SyncAuthnr mAuthenticator;


    @Override
    public void onCreate() {
        mAuthenticator = new SyncAuthnr(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
