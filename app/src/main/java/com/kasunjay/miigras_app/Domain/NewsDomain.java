package com.kasunjay.miigras_app.Domain;

public class NewsDomain {

    private String title;
    private String subTitle;
    private String picAddress;

    public NewsDomain(String title, String subTitle, String picAddress) {
        this.title = title;
        this.subTitle = subTitle;
        this.picAddress = picAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }
}
