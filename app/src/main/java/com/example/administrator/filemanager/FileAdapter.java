package com.example.administrator.filemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FileAdapter extends ArrayAdapter<FileData> {
    private int resourseId;
    private Context mContext;

    public FileAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FileData> objects) {
        super(context, resource, objects);
        resourseId = resource;
        mContext = context;
    }

    @Nullable
    @Override
    public FileData getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        FileData fileData = getItem(position);
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(resourseId, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.file_image);
            viewHolder.textView = convertView.findViewById(R.id.file_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(fileData.getName());
        File file = new File(fileData.getPath());
        if ("return to root".equals(fileData.getName()) || "return to parent".equals(fileData.getName()))
            viewHolder.imageView.setImageResource(R.drawable.back);
        else if (file.isDirectory())
            viewHolder.imageView.setImageResource(R.drawable.directory);
        else if (file.isFile()) {
            viewHolder.imageView.setImageResource(R.drawable.file);
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView textView;
        private ImageView imageView;
    }
}
