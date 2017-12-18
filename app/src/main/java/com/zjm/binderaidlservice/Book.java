package com.zjm.binderaidlservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhujiaming on 2017/12/17.
 */

public class Book implements Parcelable{
    private int bookId;
    private String author;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    protected Book(Parcel in) {
        bookId = in.readInt();
        author = in.readString();
    }

    public Book(int bookId, String author) {
        this.bookId = bookId;
        this.author = author;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(author);
    }
}
