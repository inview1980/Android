package com.inview;

public class MyItem2 {
    @ColumnName("姓名")
    private String name;
    private String details;
    private int cordNumber;

    public MyItem2(String name, String details, int cordNumber, int id) {
        this.name = name;
        this.details = details;
        this.cordNumber = cordNumber;
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

    public int getCordNumber() {
        return cordNumber;
    }

    public void setCordNumber(int cordNumber) {
        this.cordNumber = cordNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    @Override
    public String toString() {
        return "MyItem2{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", cordNumber=" + cordNumber +
                ", id=" + id +
                '}';
    }

    public MyItem2() {
    }
}
