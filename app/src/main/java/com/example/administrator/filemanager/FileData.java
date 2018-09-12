package com.example.administrator.filemanager;

public class FileData {
    public String name;
    public String path;

    public FileData() {
    }
public FileData(String name ,String path){
        this.name = name;
        this.path = path;
}
    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
