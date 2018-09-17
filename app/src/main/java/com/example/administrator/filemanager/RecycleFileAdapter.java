package com.example.administrator.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class RecycleFileAdapter extends RecyclerView.Adapter<RecycleFileAdapter.MyViewHolder> {
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private ArrayList<FileData> mFileDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItenClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    //    private boolean isLongClick;//值传递不是引用传递
    private Map<String, Boolean> isLongClick;
    private Map<Integer, Boolean> selects;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItenClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

     RecycleFileAdapter(Context context, ArrayList<FileData> fileDataList,
                              Map<String, Boolean> isLongClick, Map<Integer, Boolean> selects) {
        mFileDataList = fileDataList;
        mContext = context;
        this.isLongClick = isLongClick;
        this.selects = selects;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.fileitem, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        FileData fileData = mFileDataList.get(position);
        File file = new File(fileData.getPath());
        if (isLongClick.get("isLongClick")) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (selects.containsKey(position)) {
                holder.mCheckBox.setChecked(selects.get(position));
            } else {
                holder.mCheckBox.setChecked(false);
            }
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }
        holder.mTextView.setText(fileData.getName());
        if (file.isDirectory() ) {
            holder.mImageView.setImageResource(R.drawable.directory);
            if (fileData.getDirectoryCount() != -1){
                holder.mFileDes.setText("文件：" + fileData.getFileCount() + "\t文件夹：" + fileData.getDirectoryCount());
            }
        } else if (file.isFile()) {
            holder.mImageView.setImageResource(R.drawable.file);
            if (fileData.getLength() != -1){
                holder.mFileDes.setText("文件大小为：" + fileData.getLength() + "KB");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFileDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener {
        ImageView mImageView;
        TextView mTextView;
        TextView mFileDes;
        CheckBox mCheckBox;
        RelativeLayout mRelativeLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            mImageView =  itemView.findViewById(R.id.file_image);
            mTextView =  itemView.findViewById(R.id.file_name);
            mFileDes =  itemView.findViewById(R.id.file_des);
            mRelativeLayout =  itemView.findViewById(R.id.file_relativelayout);
            mCheckBox = itemView.findViewById(R.id.select_file);

            // 由于监听是给View的,View不会变,变得只是条件(ViewHolder的position)
            // 所以逻辑上也应该是只添加一次，然后根据条件执行动作
            initListeners();
        }

        private void initListeners() {
            // 让当前类直接继承XXListener,能够避免对象的创建,之后在回调中判断是哪个View的回调就可以
//            mCheckBox.setOnCheckedChangeListener(this);
            //TODO 先取消对复选框的状态改变监听
            mRelativeLayout.setOnClickListener(this);
            mRelativeLayout.setOnLongClickListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == mCheckBox) {
                if (mCheckBox.isChecked()) {
                    selects.put(getAdapterPosition(), false);

                } else {
                    selects.put(getAdapterPosition(), true);
                }
            }
        }
        @Override
        public void onClick(View v) {
            mOnItenClickListener.onItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mOnItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            return true;
        }
    }


}

