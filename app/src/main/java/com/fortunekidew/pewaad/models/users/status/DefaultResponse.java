package com.fortunekidew.pewaad.models.users.status;

/**
 * Created by Brian Mwadime on 09/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */
public class DefaultResponse {
    private boolean success;
    private String message;

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
