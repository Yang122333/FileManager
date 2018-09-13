package com.example.administrator.filemanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int ROOT_FILE = 0;
    public static final int PRE_FILE = 1;
    public static final int PRE_NUMBER = 2;

    //    private ListView listView;
    private ArrayList<FileData> lists;
    //    private FileAdapter fileAdapter;
    private RecyclerView recyclerView;
    private RecycleFileAdapter recycleFileAdapter;
    FileData currentFileData;
    File currentFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        listView = (ListView) findViewById(R.id.fileList);
        init();
    }

    private void init() {
        requestPower();
        showData(File.separator);//root
    }

    private void showData(String path) {
        lists = new ArrayList<>();
        int directoryCount = 0;
        currentFile = new File(path);

        if (!"/".equals(path)) {
            directoryCount = 2;//为两个返回空下位置
            FileData rootFile = new FileData();
            rootFile.setName("return to root");
            rootFile.setPath(File.separator);
            lists.add(rootFile);
            FileData preFile = new FileData();
            preFile.setName("return to parent");
            preFile.setPath(currentFile.getParent());
            lists.add(preFile);
        }
        File[] files = currentFile.listFiles();
        if (files != null)
            for (File f : files) {
                FileData fileData = new FileData(f.getName(), f.getAbsolutePath());

                if (f.isDirectory()) {
                    lists.add(directoryCount, fileData);
                    directoryCount++;
                } else {
                    lists.add(fileData);
                }
            }
//        fileAdapter = new FileAdapter(this, R.layout.fileitem, lists);
//        listView.setAdapter(fileAdapter);

        recycleFileAdapter = new RecycleFileAdapter(lists, this);
        recyclerView.setAdapter(recycleFileAdapter);
        recycleFileAdapter.setOnItemClickListener(new MyItemClickListener());
//        listView.setOnItemClickListener(new MyItemClickListener());
    }

    private class MyItemClickListener implements OnRecyclerViewItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            currentFileData = lists.get(position);
            File currentFile = new File(currentFileData.getPath());
            if (currentFile.isDirectory()) {
                showData(currentFile.getAbsolutePath());
            } else {
//                openFile(file.getPath());
            }
        }
    }

//    private class MyItemClickListener implements AdapterView.OnItemClickListener {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            FileData currentFileData = lists.get(position);
//            File file = new File(currentFileData.getPath());
//            if (file.isDirectory()) {
//                showData(file.getAbsolutePath());
//            } else {
////                openFile(file.getPath());
//            }
//        }
//    }
    @Override
    public void onBackPressed() {

//        System.out.println("按下了back键   onBackPressed()");
        FileData fileData = null;
        if (lists.size() >= PRE_NUMBER) {
            fileData = lists.get(PRE_FILE);  // 获取父文件夹信息与路径
        }
        if (fileData != null) {
            //如果父亲节点是根目录则提示退出
            if (File.separator.equals(currentFile.getPath())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("是否退出")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
            } else {
                showData(fileData.getPath());
            }
        }

    }
    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        }
    }
}
