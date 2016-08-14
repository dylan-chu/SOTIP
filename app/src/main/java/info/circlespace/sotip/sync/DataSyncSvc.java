/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This class implements a service to run the sync adapter.
 * <p/>
 * The code in this class is based on code in Udacity's Advanced Android Development course's Github repo.
 */
public class DataSyncSvc extends Service {
    public static final String LOG_TAG = DataSyncSvc.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static DataSyncAdptr sSyncAdapter = null;


    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new DataSyncAdptr(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
