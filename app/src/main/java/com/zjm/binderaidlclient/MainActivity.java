package com.zjm.binderaidlclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.zjm.binderaidlservice.Book;
import com.zjm.binderaidlservice.IBookManager;
import com.zjm.binderaidlservice.IOnBookListChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

/*    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBookManager = IBookManager.Stub.asInterface(service);
            if (mIBookManager != null) {
                hint("connect Service Success");
                try {
                    service.linkToDeath(mDeathRecipient, 0);
                    mIBookManager.registListener(bookChangedListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                hint("connect Service failed");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            hint("onServiceDisconnected Service");
            mIBookManager=null;
        }
    };*/

/*    private DeathRecipient mDeathRecipient = new DeathRecipient() {
        @Override
        public void binderDied() {
            hint("binderDied Service");

            try {
                if (mIBookManager != null) {
                    mIBookManager.unRegistListener(bookChangedListener);
                }
                hint("2s后重新尝试绑定");
                connToServiceDelay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };*/

    private IOnBookListChangedListener bookChangedListener = new IOnBookListChangedListener.Stub() {
        @Override
        public void onChanged(List<Book> newBookList) throws RemoteException {
            refreshAdapter(newBookList);
        }
    };

    private IBookManager mIBookManager;
    private EditText etId;
    private ListView lvBooks;
    private List<String> mBooks;
    private ArrayAdapter<String> mAdapter;
    private IBinder mBookManagerBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        connToService();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            unbindService(conn);
//        } catch (IllegalArgumentException e) {
//            hint("error:" + e.getMessage());
//        }
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addRandomBook();
                break;
            case R.id.btn_del:
                delBookById();
                break;
            case R.id.btn_refresh:
                refresh();
                break;
            case R.id.btn_rebind:
//                connToService();

                break;
            case R.id.btn_unbind:
//                try {
//                    unbindService(conn);
//                } catch (IllegalArgumentException e) {
//                    hint("error:" + e.getMessage());
//                }
                break;
        }
    }

    private void findViews() {
        etId = findViewById(R.id.et_id);
        lvBooks = findViewById(R.id.lv);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvBooks.setAdapter(mAdapter);
    }

    private void refresh() {
        if (mIBookManager != null) {
            try {
                List<Book> bookList = mIBookManager.getBookList();
                refreshAdapter(bookList);
            } catch (RemoteException e) {
                hint("getBookList RemoteException:" + e.getMessage());
                e.printStackTrace();
            }
        }else{
            hint("mIBookManager is null");
        }
    }

    private Book addRandomBook() {
        int id = new Random().nextInt(100);
        String name = "第" + new Random().nextInt(1024) + "夜想你";
        Book book = new Book(id, name);
        if (mIBookManager != null) {
            try {
                mIBookManager.addBook(book);
            } catch (RemoteException e) {
                hint("addBook RemoteException:" + e.getMessage());
                e.printStackTrace();
            }
        }else{
            hint("mIBookManager is null");
        }
        return book;
    }

    private void delBookById() {
        String idStr = etId.getText().toString();
        if (TextUtils.isEmpty(idStr)) {
            hint("请输入id");
            return;
        }
        int id = Integer.parseInt(idStr);
        if (mIBookManager != null) {
            try {
                if (mIBookManager.delBookById(id)) {
                    hint("删除成功");
                } else {
                    hint("删除失败");
                }
            } catch (RemoteException e) {
                hint("delBookById RemoteException:" + e.getMessage());
                e.printStackTrace();
            }
        }else{
            hint("mIBookManager is null");
        }
    }


    private void refreshAdapter(List<Book> books) {
        if (books == null) {
            hint("book list is null!");
            return;
        }
        List<String> bookStrs = new ArrayList<>();
        for (Book book : books) {
            bookStrs.add(book.getBookId() + "    " + book.getAuthor());
        }
        mAdapter.clear();
        mAdapter.addAll(bookStrs);
        mAdapter.notifyDataSetChanged();
    }

    public void connToServiceDelay(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connToService();
            }
        }, 2000);
    }

    private void connToService() {
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.zjm.binderaidlservice", "com.zjm.binderaidlservice.BookManagerService"));
//        boolean result = bindService(intent, conn, BIND_AUTO_CREATE);
//        hint("bindService："+result);
        new Thread(new Runnable() {
            @Override
            public void run() {
                hint("尝试获取 bookManagerBinder");
//                mIBookManager = (IBookManager) BinderPool.getInstance(MainActivity.this).queryBinder(BinderPool.BINDER_BOOKS);
                IBinder iBinder = BinderPool.getInstance(MainActivity.this).queryBinder(BinderPool.BINDER_BOOKS);
                mIBookManager = IBookManager.Stub.asInterface(iBinder);
                if (mIBookManager != null) {
                    hint("bookManagerBinder 获取成功");
                    try {
                        mIBookManager.registListener(bookChangedListener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }else{
                    hint("bookManagerBinder 获取失败");
                }
            }
        }).start();
    }

    private void hint(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.i("zhujm", "client: " + msg);
            }
        });

    }

    private void unbind() {

    }
}
