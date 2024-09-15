package com.kasunjay.miigras_app.data.model;

import java.util.Date;

public class ChatMessage {
    private String message;
    private String senderId;
    private String receiverId;
    private String dateTime;
    private Date dateObject;
    private String conversionId;
    private String conversionName;

    public ChatMessage() {
    }

    public ChatMessage(String message, String senderId, String receiverId, String dateTime, Date dateObject, String conversionId, String conversionName) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.conversionId = conversionId;
        this.conversionName = conversionName;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getConversionName() {
        return conversionName;
    }

    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
