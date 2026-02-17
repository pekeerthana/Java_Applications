package com.example.demo.model;

public class ErrorRequest {

    private String message;
    private int statusCode;
    private String date;

    public ErrorRequest(String message, int statusCode, String date){
        this.message = message;
        this.statusCode = statusCode;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    
}
