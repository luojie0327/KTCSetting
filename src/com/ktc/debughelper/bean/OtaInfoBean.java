package com.ktc.debughelper.bean;

public class OtaInfoBean {
    String fieldName;
    String fieldDesc;
    String fieldValue;
    Boolean isTitle;

    public OtaInfoBean(String fieldName, String fieldDesc, String fieldValue, Boolean isTitle) {
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
        this.fieldValue = fieldValue;
        this.isTitle = isTitle;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Boolean getTitle() {
        return isTitle;
    }

    public void setTitle(Boolean title) {
        isTitle = title;
    }
}