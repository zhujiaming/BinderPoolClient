package com.zjm.binderaidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.zjm.binderaidlservice.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhujiaming on 2017/12/18.
 */

public class BinderPool {
    public static final int BINDER_BOOKS = 0x0010;
    public static final int BINDER_NULL = -1;

    private Context mContext;
    private IBinderPool mBinderPool;

    private static volatile BinderPool sInstance;
    private CountDownLatch mConnectPoolCountDownLatch;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);

            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (mConnectPoolCountDownLatch != null) {
                mConnectPoolCountDownLatch.countDown();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //ignore
        }
    };


    public BinderPool(Context context) {
        this.mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                sInstance = new BinderPool(context);
            }
        }
        return sInstance;

    }

    private synchronized void connectBinderPoolService() {
        mConnectPoolCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.zjm.binderaidlservice", "com.zjm.binderaidlservice.BookManagerService"));
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        try {
            mConnectPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        if (mBinderPool != null) {
            try {
                return mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void unBindService() {
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
        }
    }

}
