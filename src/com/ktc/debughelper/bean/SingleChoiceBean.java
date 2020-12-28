package com.ktc.debughelper.bean;

public class SingleChoiceBean {
    String itemName;
    Boolean curItemSelected;

    public SingleChoiceBean(String itemName, Boolean curItemSelected) {
        this.itemName = itemName;
        this.curItemSelected = curItemSelected;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Boolean getCurItemSelected() {
        return curItemSelected;
    }

    public void setCurItemSelected(Boolean curItemSelected) {
        this.curItemSelected = curItemSelected;
    }
}