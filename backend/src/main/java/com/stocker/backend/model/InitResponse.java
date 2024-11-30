package com.stocker.backend.model;

import lombok.Setter;

@Setter
public class InitResponse {

    private String message;
    private int status;

    public InitResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    // 게터 및 세터
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
