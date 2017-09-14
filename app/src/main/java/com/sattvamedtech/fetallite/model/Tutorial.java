package com.sattvamedtech.fetallite.model;

public class Tutorial {
    public int id;
    public String title;
    public String fileName;
    public boolean isVideo;

    public Tutorial(int id, String title, String fileName, boolean isVideo) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.isVideo = isVideo;
    }
}
