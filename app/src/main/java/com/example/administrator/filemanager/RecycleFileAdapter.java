package com.example.administrator.filemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        this.mOnItenClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public RecycleFileAdapter(Context context, ArrayList<FileData> fileDataList,
                              Map<String, Boolean> isLongClick, Map<Integer, Boolean> selects) {

        mFileDataList = fileDataList;
        mContext = context;
        this.isLongClick = isLongClick;
        this.selects = selects;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.fileitem, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        FileData fileData = mFileDataList.get(position);
        File file = new File(fileData.getPath());
        if (isLongClick.get("isLongClick")) {
            if (!mFileDataList.get(position).getName().equals(MainActivity.RETURN_TO_BACK) &&
                    !mFileDataList.get(position).getName().equals(MainActivity.RETURN_TO_ROOT)) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
            }
            if (selects.containsKey(position)) {
                holder.mCheckBox.setChecked(selects.get(position));
            } else {
                holder.mCheckBox.setChecked(false);
            }
            holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mCheckBox.isChecked()) {
                        selects.put(position, false);

                    } else {
                        selects.put(position, true);
                    }
                }
            });
        }
        holder.mTextView.setText(fileData.getName());
        if (file.isDirectory()){
            holder.mImageView.setImageResource(R.drawable.directory);
            holder.mFileDes.setText("文件："+fileData.getFileCount()+"\t文件夹："+fileData.getDirectoryCount());
        }
            else if (file.isFile()) {
            holder.mFileDes.setText("文件大小为："+fileData.getLength()+"KB");
            holder.mImageView.setImageResource(R.drawable.file);
        }

        //手动为recycleFileAdapter添加item监听器
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItenClickListener.onItemClick(v, position);
            }
        });
        holder.mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemLongClickListener.onItemLongClick(v, position);
                return true;//消费事件
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFileDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        TextView mFileDes;
        CheckBox mCheckBox;
        RelativeLayout mRelativeLayout;


        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.file_image);
            mTextView = (TextView) itemView.findViewById(R.id.file_name);
            mFileDes = (TextView) itemView.findViewById(R.id.file_des);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.file_relativelayout);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.select_file);
        }

    }


}

