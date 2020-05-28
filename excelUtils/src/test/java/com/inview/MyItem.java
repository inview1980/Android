package com.inview;

public class MyItem {
    @ColumnName("姓名")
    private String name;
    private String details;
    private int id;

    public MyItem() {
    }

    @Override
    public String toString() {
        return "MyItem{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", id=" + id +
                '}';
    }

    public MyItem(String name, String details, int id) {
        this.name = name;
        this.details = details;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
