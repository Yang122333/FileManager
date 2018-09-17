package com.example.administrator.filemanager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String MKEY = "isLongClick";

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
    private ArrayList<FileData> lists = new ArrayList<>();
    private FileData currentFileData;
    private File currentFile;
    private Map<String, Boolean> isLongClick = new HashMap<>();
    private Handler handler;
    @SuppressLint("UseSparseArrays")
    private final Map<Integer, Boolean> selects = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        RequestPower.requestPower(this);
        initData();
        initView();
        initListener();
    }

    @SuppressLint("CutPasteId")
    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        uiLinearLayout = findViewById(R.id.ui_linearLayout);
        createFileBtn = findViewById(R.id.create_directory);
        searchFileBtn = findViewById(R.id.search_file);

        operateFileLinearlayout = findViewById(R.id.operate_file_linearlayout);
        copyFileBtn = findViewById(R.id.copy_file);
        cutFileBtn = findViewById(R.id.cut_file);
        deleteFileBtn = findViewById(R.id.delete_file);
        choseAll = findViewById(R.id.chose_all);
        cancelChoseAll = findViewById(R.id.cancel_chose_all);

        copyAndCutRelativeLayout = findViewById(R.id.copy_and_cut_file_relativelayout);
        newdirectory = findViewById(R.id.create_directory);
        cancelMoveFileBtn = findViewById(R.id.cancel);
        pasteFileBtn = findViewById(R.id.paste_file);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recycleFileAdapter = new RecycleFileAdapter(this, lists, isLongClick, selects);
        recyclerView.setAdapter(recycleFileAdapter);
        recycleFileAdapter.setOnItemClickListener(new MyItemClickListener());
        recycleFileAdapter.setOnItemLongClickListener(new MyItemLongClickListener());
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        isLongClick.put(MKEY, false);
        showData(File.separator);//root
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                recycleFileAdapter.notifyDataSetChanged();
            }
        };
    }

    private void initListener() {
        createFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = View.inflate(MainActivity.this, R.layout.dialog, null);
                builder.setView(view);
                final EditText editText = view.findViewById(R.id.create_file_name);
                Button cancelBtn = view.findViewById(R.id.dialog_cancel);
                Button submitBtn = view.findViewById(R.id.dialog_save);
                final Dialog dialog = builder.create();
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = getString(R.string.match);
                        if (editText.getText().toString().matches(str)) {
                            File file = new File(currentFile.getAbsolutePath() + File.separator + editText.getText().toString());
                            if (!file.exists()) {
                                if (!file.mkdirs()) {
                                    Toast.makeText(MainActivity.this, "无法创建", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "该文件夹已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                        showData(currentFile.getAbsolutePath());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
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


    @SuppressLint("HandlerLeak")
    private void showData(String path) {
        lists.clear();
        currentFile = new File(path);
        File[] filesArray = currentFile.listFiles();
        if (filesArray != null) {
            for (File aFilesArray : filesArray) {
                FileData fileData = new FileData(aFilesArray.getName(), aFilesArray.getAbsolutePath());
                lists.add(fileData);
            }
        }
        getMessage(currentFile);
        if (recycleFileAdapter != null)
            recycleFileAdapter.notifyDataSetChanged();
    }

    public void getMessage(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File[] filesArray = file.listFiles();
                for (int i = 0; i < filesArray.length; i++) {
                    if (file.isDirectory()) {
                        int[] counts = FileTool.getFileAndDirectoryCount(filesArray[i]);
                        lists.get(i).setDirectoryCount(counts[FileTool.DIRECTORY_COUNT]);
                        lists.get(i).setFileCount(FileTool.FILE_COUNT);
                    } else if (file.isFile()) {
                        lists.get(i).setLength(file.length());
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void chose(boolean iscChosed) {
        for (int i = 0; i < FileTool.getFileCount(currentFile); i++) {
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
        public void onItemClick(View view, final int position) {
            if (!isLongClick.get(MKEY)) {
                currentFileData = lists.get(position);
                currentFile = new File(currentFileData.getPath());
                if (currentFile.isDirectory()) {
                    showData(currentFile.getAbsolutePath());
                } else {
                    FileTool.openFile(currentFile.getPath(), MainActivity.this);
                }
            } else {
                CheckBox checkBox = view.findViewById(R.id.select_file);
                //TODO  无法和适配器里的复选框状态改变监听一起使用，暂时取消适配器中的监听，点击item才有效
                if (checkBox.isChecked()) {
                    selects.put(position, false);
                } else {
                    selects.put(position, true);
                }
            }
            recycleFileAdapter.notifyDataSetChanged();
        }
    }

    public void delete() {
        boolean flag = false;
        for (Map.Entry<Integer, Boolean> entry : selects.entrySet()) {
            currentFileData = lists.get(entry.getKey());
            currentFile = new File(currentFileData.getPath());
            if (currentFile.exists() && entry.getValue()) {
                if (currentFile.isDirectory()) {
                    flag = FileTool.deleteFile(currentFile);
                } else if (currentFile.isFile()) {
                    flag = FileTool.deleteFile(currentFile);
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
    }


    public void turnToBack() {
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


//   ViewGroup.LayoutParams


}
