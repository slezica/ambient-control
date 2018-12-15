package io.slezica.ambientcontrol.utils;

import android.util.Log;

public class TaggedLog {

    private final String tag;

    public TaggedLog(Object any) {
        this.tag = any.getClass().getSimpleName();
    }

    public void v(String message) {
        Log.v(tag, message);
    }

    public void d(String message) {
        Log.d(tag, message);
    }

    public void e(String message, Throwable error) {
        Log.e(tag, message, error);
    }

}
