// IBookManager.aidl
package com.zjm.binderaidlservice;
import com.zjm.binderaidlservice.Book;
import com.zjm.binderaidlservice.IOnBookListChangedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     void addBook(in Book book);

     List<Book> getBookList();

     Book getBookById(int id);

     boolean delBookById(int id);

     void registListener(IOnBookListChangedListener listener);

     void unRegistListener(IOnBookListChangedListener listener);
}
