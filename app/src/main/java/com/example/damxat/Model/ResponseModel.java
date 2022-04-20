package com.example.damxat.Model;

public class ResponseModel {
    public int succes;
    public int failure;

    public ResponseModel() {
    }

    public ResponseModel(int succes, int failure) {
        this.succes = succes;
        this.failure = failure;
    }

    public int getSucces() {
        return succes;
    }

    public void setSucces(int succes) {
        this.succes = succes;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }
}
