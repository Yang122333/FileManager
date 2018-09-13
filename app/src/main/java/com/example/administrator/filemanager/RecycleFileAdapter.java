package com.example.administrator.filemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class RecycleFileAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private ArrayList<FileData> mFileDataList;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItenClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public RecycleFileAdapter(ArrayList<FileData> fileDataList, Context context) {
        mFileDataList = fileDataList;
        mContext = context;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItenClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fileitem, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        FileData fileData = mFileDataList.get(position);
        File file = new File(fileData.getPath());
        holder.mTextView.setText(fileData.getName());
        if ("return to root".equals(fileData.getName()) || "return to parent".equals(fileData.getName()))
            holder.mImageView.setImageResource(R.drawable.back);
        else if (file.isDirectory())
            holder.mImageView.setImageResource(R.drawable.directory);
        else if (file.isFile()) {
            holder.mImageView.setImageResource(R.drawable.file);
        }

        //手动为recycleFileAdapter添加item监听器
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItenClickListener.onItemClick(v,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFileDataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView mImageView;
    TextView mTextView;
    LinearLayout mLinearLayout;

    public MyViewHolder(View itemView) {
        super(itemView);
        mImageView = (ImageView) itemView.findViewById(R.id.file_image);
        mTextView = (TextView) itemView.findViewById(R.id.file_name);
        mLinearLayout = (LinearLayout) itemView.findViewById(R.id.file_linearlayout);
    }



}
interface OnRecyclerViewItemClickListener {
    void onItemClick(View view,int position);
}

 interface OnRecyclerViewItemLongClickListener {
    void onItemLongClick(View view,int position);
}