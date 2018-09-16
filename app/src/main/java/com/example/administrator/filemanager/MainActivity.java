package com.example.administrator.filemanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int ROOT_FILE = 0;
    public static final int PRE_FILE = 1;
    public static final int PRE_COUNT = 2;
    public static final String RETURN_TO_BACK = "return to parent";
    public static final String RETURN_TO_ROOT = "return to root";

    private static final String MKEY = "isLongClick";

    private RecyclerView recyclerView;
    private LinearLayout uiLinearLayout;
    private Button createFileBtn;
    private Button searchFileBtn;

    private LinearLayout operateFileLinearlayout;
    private Button copyFileBtn;
    private Button cutFileBtn;
    private Button deleteFileBtn;
    private Button choseAll;
    private Button cancelChoseAll;

    private RelativeLayout copyAndCutRelativeLayout;
    private Button newdirectory;
    private Button cancelMoveFileBtn;
    private Button pasteFileBtn;

    private RecycleFileAdapter recycleFileAdapter;
    private ArrayList<FileData> lists;
    private FileData currentFileData;
    private File currentFile;
    private Map<String, Boolean> isLongClick = new HashMap<>();

    private Map<Integer, Boolean> selects = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        uiLinearLayout = (LinearLayout) findViewById(R.id.ui_linearLayout);
        createFileBtn = (Button) findViewById(R.id.create_directory);
        searchFileBtn = (Button) findViewById(R.id.search_file);

        operateFileLinearlayout = (LinearLayout) findViewById(R.id.operate_file_linearlayout);
        copyFileBtn = (Button) findViewById(R.id.copy_file);
        cutFileBtn = (Button) findViewById(R.id.cut_file);
        deleteFileBtn = (Button) findViewById(R.id.delete_file);
        choseAll = (Button) findViewById(R.id.chose_all);
        cancelChoseAll = (Button) findViewById(R.id.cancel_chose_all);

        copyAndCutRelativeLayout = (RelativeLayout) findViewById(R.id.copy_and_cut_file_relativelayout);
        newdirectory = (Button) findViewById(R.id.create_directory);
        cancelMoveFileBtn = (Button) findViewById(R.id.cancel);
        pasteFileBtn = (Button) findViewById(R.id.paste_file);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        isLongClick.put(MKEY, false);
        requestPower();
        showData(File.separator);//root
    }

    private void initListener() {
//        copyFileBtn.setOnClickListener();
//        cutFileBtn.setOnClickListener();
        deleteFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("是否删除？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
            }
        });
        choseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chose(true);
                recycleFileAdapter.notifyDataSetChanged();
                choseAll.setVisibility(View.GONE);
                cancelChoseAll.setVisibility(View.VISIBLE);
            }
        });
        cancelChoseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chose(false);
                recycleFileAdapter.notifyDataSetChanged();
                choseAll.setVisibility(View.VISIBLE);
                cancelChoseAll.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isLongClick.get(MKEY)) {
            cancel();
        } else {
            turnToBack();
        }
    }

    private void cancel() {
        operateFileLinearlayout.setVisibility(View.GONE);
        copyAndCutRelativeLayout.setVisibility(View.GONE);
        uiLinearLayout.setVisibility(View.VISIBLE);
        isLongClick.put(MKEY, false);
        selects.clear();
        showData(currentFile.getAbsolutePath());
    }

    /**
     * 展示文件及文件夹
     *
     * @param path
     */
    private void showData(String path) {
        lists = new ArrayList<>();
        int directoryIndex = 0;
        currentFile = new File(path);
        File[] files = currentFile.listFiles();
        // TODO 所有的条件循环等都应该有大括号, 哪怕是一行
        if (files != null)
            for (File f : files) {
                FileData fileData = new FileData(f.getName(), f.getAbsolutePath());
                if (f.isDirectory()) {
                    int directoryCount = 0;
                    int fileCount = 0;
                    File[] countFile = f.listFiles();
                    if (countFile != null)
                        for (File f1 : countFile) {
                            if (f1.isDirectory()) {
                                directoryCount++;
                            }
                            if (f1.isFile()) {
                                fileCount++;
                            }
                        }
                    fileData.setFileCount(fileCount);
                    fileData.setDirectoryCount(directoryCount);
                    lists.add(directoryIndex, fileData);
                    directoryIndex++;
                } else {
                    fileData.setLength(f.length());
                    lists.add(fileData);
                }
            }
        // TODO 每showData一次new个Adapter?尽量减少对象创建,重用一下上次的Adapter
        recycleFileAdapter = new RecycleFileAdapter(this, lists, isLongClick, selects);
        recyclerView.setAdapter(recycleFileAdapter);
        recycleFileAdapter.setOnItemClickListener(new MyItemClickListener());
        recycleFileAdapter.setOnItemLongClickListener(new MyItemLongClickListener());
    }

    // TODO 像这种方法内没有引用类的成员变量的,都是可以改为static的
    // TODO 把这种方法按功能全部独立封装到其他类中
    private int getFileCount(File file) {
        int count = 0;
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null)
                count = files.length;
        }
        return count;
    }

    private void chose(boolean iscChosed) {
        for (int i = 0; i < getFileCount(currentFile); i++) {
            selects.put(i, iscChosed);
        }
    }

    private class MyItemLongClickListener implements RecycleFileAdapter.OnRecyclerViewItemLongClickListener {
        @Override
        public void onItemLongClick(View view, final int position) {
            isLongClick.put(MKEY, true);
            uiLinearLayout.setVisibility(View.GONE);
            operateFileLinearlayout.setVisibility(View.VISIBLE);
            selects.put(position, true);
            recycleFileAdapter.notifyDataSetChanged();
        }
    }

    private class MyItemClickListener implements RecycleFileAdapter.OnRecyclerViewItemClickListener {
        @Override
        public void onItemClick(View view, int position) {
            if (!isLongClick.get(MKEY)) {

                currentFileData = lists.get(position);
                currentFile = new File(currentFileData.getPath());
                if (currentFile.isDirectory()) {
                    showData(currentFile.getAbsolutePath());
                } else {
                    openFile(currentFile.getPath());
                }
            } else {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.select_file);
                if (checkBox.isChecked())
                    selects.put(position, false);
                else {
                    selects.put(position, true);
                }
                recycleFileAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean delete() {
        boolean flag = false;
        for (Map.Entry<Integer, Boolean> entry : selects.entrySet()) {
            currentFileData = lists.get(entry.getKey());
            currentFile = new File(currentFileData.getPath());

            if (currentFile.exists() && currentFile.canWrite() && entry.getValue()) {
                currentFile.delete();
                if (currentFile.isDirectory()) {
                    flag = deleteDirectory(currentFile);
                } else if (currentFile.isFile()){
                    flag = deleteFile(currentFile);
                }
            } else {
                flag = false;
            }
        }
        if (!flag) {
            Toast.makeText(this, "无法对该文件进行操作", Toast.LENGTH_SHORT).show();
            currentFile = currentFile.getParentFile();
        } else {
            selects.clear();
            String preFilePath = currentFile.getParent();
            showData(preFilePath);
        }
        return flag;
    }

    // TODO 像这种方法内没有引用类的成员变量的,都是可以改为static的
    // TODO 把这种方法按功能全部独立封装到其他类中
    private static boolean deleteFile(File file) {
        boolean flag = false;
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;

    }

    // TODO 像这种方法内没有引用类的成员变量的,都是可以改为static的
    // TODO 把这种方法按功能全部独立封装到其他类中
    private boolean deleteDirectory(File dirFile) {
        boolean flag = false;
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i]);
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public void turnToBack() {

        if (currentFile.getPath() != File.separator) {
            //如果当前节点是根目录，弹出对话框
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
                showData(currentFile.getParent());
            }
        }
    }

    /**
     * 打开文件
     *
     * @param path
     */
    // TODO 像这种方法内没有引用类的成员变量的,都是可以改为static的
    // TODO 把这种方法按功能全部独立封装到其他类中
    public void openFile(String path) {
        if (path == null)
            return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String fileType = "";

        for (int i = 0; i < FormatData.MATCH_ARRAY.length; i++) {
            if (path.contains(FormatData.MATCH_ARRAY[i][0])) {
                fileType = FormatData.MATCH_ARRAY[i][1];
                break;
            }
        }
        intent.setDataAndType(Uri.fromFile(new File(path)), fileType);
        // TODO 上面说的没有引用类的成员变量,引用了公开方法的可以加个参数
        startActivity(intent);
    }

    // TODO 像这种方法内没有引用类的成员变量的,都是可以改为static的
    // TODO 把这种方法按功能全部独立封装到其他类中
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
//   ViewGroup.LayoutParams


}
