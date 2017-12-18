// IBinderPool.aidl
package com.zjm.binderaidlservice;

// Declare any non-default types here with import statements

interface IBinderPool {
   IBinder queryBinder(in int binderCode);
}
