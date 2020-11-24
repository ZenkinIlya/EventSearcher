package com.startup.eventsearcher.utils;

import android.util.Log;

public class ErrorServerHandler {

    private int code;
    private String description;

    public ErrorServerHandler(String tag, int code, String description) {
        this.code = code;
        this.description = description;
        Log.d(tag, "Code = " + code + " Description: " + description);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
