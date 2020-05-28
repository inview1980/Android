package com.inview;

public class MyItem3 {
    private String name;
    private String details;
    private int cordNumber;

    @Override
    public String toString() {
        return "MyItem3{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", cordNumber=" + cordNumber +
                '}';
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
}
