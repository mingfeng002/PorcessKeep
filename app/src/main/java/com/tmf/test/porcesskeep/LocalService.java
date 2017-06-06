package com.tmf.test.porcesskeep;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class LocalService extends Service {
    private MyBilder mBilder;
    private LocalServiceConnection connection;
    @Override
    public void onCreate() {
        super.onCreate();
        mBilder = new MyBilder();
        connection=new LocalServiceConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(LocalService.this, RemoteService.class),connection, Context.BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }

    private class MyBilder extends IGuardAidl.Stub {
        @Override
        public void doSomething() throws RemoteException {
            Log.i("TAG", "local service 绑定成功!");
        }
    }

    private class LocalServiceConnection implements  ServiceConnection{
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("TAG", "RemoteService被杀死了");
            LocalService.this.startService(new Intent(LocalService.this,RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this,RemoteService.class), connection,Context.BIND_IMPORTANT);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("TAG", "RemoteService链接成功!");
            try {
                if (mBilder != null)
                    mBilder.doSomething();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}