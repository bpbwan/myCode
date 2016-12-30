package com.android.launcher2.util;

public class FavoritesEntity {
	
	private String type; //default_workspace.xml中的标签:appwidget,favorite等
	
    private String className;
    private String packageName;
    private String screen;
    private String x;
    private String y;
    private String spanX;
    private String spanY;
    private String icon;
    private String title;
    private String uri;
    private String move;
    private String delete;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getScreen() {
		return screen;
	}
	public void setScreen(String screen) {
		this.screen = screen;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getSpanX() {
		return spanX;
	}
	public void setSpanX(String spanX) {
		this.spanX = spanX;
	}
	public String getSpanY() {
		return spanY;
	}
	public void setSpanY(String spanY) {
		this.spanY = spanY;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getMove() {
		return move;
	}
	public void setMove(String move) {
		this.move = move;
	}
    public String getDelete() {
        return delete;
    }
    public void setDelete(String delete) {
        this.delete = delete;
    }
    @Override
    public String toString() {
        return "FavoritesEntity [type=" + type + ", className=" + className
                + ", packageName=" + packageName + ", screen=" + screen
                + ", x=" + x + ", y=" + y + ", spanX=" + spanX + ", spanY="
                + spanY + ", icon=" + icon + ", title=" + title + ", uri="
                + uri + ", move=" + move + ", delete=" + delete + "]";
    }
	 
}
