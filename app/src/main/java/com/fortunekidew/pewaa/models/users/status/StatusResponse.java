package com.fortunekidew.pewaa.models.users.status;

/**
 * Created by Abderrahim El imame on 03/05/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class StatusResponse {
    private boolean success;
    private String message;
    private String id;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
