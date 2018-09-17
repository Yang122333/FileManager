package com.example.administrator.filemanager;

public class FileData {
    private String name;
    private String path;
    private int fileCount = -1;
    private int directoryCount = -1;
    private long length = -1;
    private String time = "";

     FileData(String name, String path) {
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

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public void setDirectoryCount(int directoryCount) {
        this.directoryCount = directoryCount;
    }

    public long getLength() {
        return length / 1024 + 1;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
