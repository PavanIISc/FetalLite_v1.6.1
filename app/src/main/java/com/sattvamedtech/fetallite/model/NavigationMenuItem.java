package com.sattvamedtech.fetallite.model;

public class NavigationMenuItem {

    public static final int VIEW_DEFAULT = 0;
    public static final int VIEW_BRIGHTNESS = 1;
    public static final int VIEW_THEME = 2;

    public int id;
    public String title;
    public boolean enable;
    public int viewType;

    public NavigationMenuItem(int id, String title, boolean enable) {
        this.id = id;
        this.title = title;
        this.enable = enable;
        this.viewType = VIEW_DEFAULT;
    }

    public NavigationMenuItem(int id, String title, boolean enable, int viewType) {
        this.id = id;
        this.title = title;
        this.enable = enable;
        this.viewType = viewType;
    }
}
