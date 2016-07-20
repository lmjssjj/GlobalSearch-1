package com.bbk.open.model;

/**
 * Created by Administrator on 2016/7/19.
 */
public class FileInfo {
    public static final int TYPE_APP = 1;
    public static final int TYPE_CONTACT = 2;
    public static final int TYPE_SMS = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_IMAGE = 5;
    public static final int TYPE_VIDEO = 6;
    public static final int TYPE_INSTALL = 7;
    public static final int TYPE_ARCHIVE = 8;
    public static final int TYPE_TXT = 9;
    public static final int TYPE_XLS = 10;
    public static final int TYPE_HTML = 11;
    public static final int TYPE_PDF = 12;
    public static final int TYPE_WORD = 13;
    public static final int TYPE_PPT = 14;

    private int _id;
    private String name;
    private String path;
    private String searchInfo;
    private String nosearchInfo;
    private int type;

    public FileInfo() {
    }

    public FileInfo(String name, String path, String searchInfo, String nosearchInfo, int type) {
        this.name = name;
        this.path = path;
        this.searchInfo = searchInfo;
        this.nosearchInfo = nosearchInfo;
        this.type = type;
    }


    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSearchInfo() {
        return searchInfo;
    }

    public String getNosearchInfo() {
        return nosearchInfo;
    }

    public int getType() {
        return type;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSearchInfo(String searchInfo) {
        this.searchInfo = searchInfo;
    }

    public void setNosearchInfo(String nosearchInfo) {
        this.nosearchInfo = nosearchInfo;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", searchInfo='" + searchInfo + '\'' +
                ", nosearchInfo='" + nosearchInfo + '\'' +
                ", type=" + type +
                '}';
    }
}
