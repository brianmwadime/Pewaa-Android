package com.sourcecanyon.whatsClone.models;

import java.util.UUID;

/**
 * Created by Abderrahim El imame on 01/11/2015.
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */
public class JoinModel {
    private boolean success;
    private boolean smsVerification;
    private String message;
    private String mobile;
    private String code;
    private String userID;
    private String token;



    public boolean isSmsVerification() {
        return smsVerification;
    }

    public void setSmsVerification(boolean smsVerification) {
        this.smsVerification = smsVerification;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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
}
