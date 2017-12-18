// IOnBookListChanged.aidl
package com.zjm.binderaidlservice;
import com.zjm.binderaidlservice.Book;
// Declare any non-default types here with import statements

interface IOnBookListChangedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onChanged(in List<Book> newBookList);
}
