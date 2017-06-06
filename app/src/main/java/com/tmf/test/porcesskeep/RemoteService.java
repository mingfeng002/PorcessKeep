package com.tmf.test.porcesskeep;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteService extends Service {
    private MyBilder mBilder;
    private RemoteServiceConnection connection;
    @Override
    public void onCreate() {
        super.onCreate();
        mBilder = new MyBilder();
        connection=new RemoteServiceConnection();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(RemoteService.this, LocalService.class),connection, Context.BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }

    private class MyBilder extends IGuardAidl.Stub {

        @Override
        public void doSomething() throws RemoteException {
            Log.i("TAG", "romate service 绑定成功!");
        }
    }

    private class RemoteServiceConnection implements  ServiceConnection{
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("TAG", "LocalService被杀死了");
            RemoteService.this.startService(new Intent(RemoteService.this,LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this,  LocalService.class), connection, Context.BIND_IMPORTANT);
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("TAG", "LocalService链接成功!");
            try {
                if (mBilder != null)
                    mBilder.doSomething();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
