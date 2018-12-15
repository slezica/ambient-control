package io.slezica.ambientcontrol.ambient;

import android.content.Context;
import io.slezica.ambientcontrol.utils.TaggedLog;

public class AmbientMock implements Ambient {

    private final TaggedLog log = new TaggedLog(this);

    private boolean isAlwaysOn;

    public AmbientMock(Context context) {
        log.d("Initialized with " + context.getClass().getSimpleName());
    }

    @Override
    public boolean hasPermissions() {
        log.d("hasPermissions called, returning true");
        return true;
    }

    @Override
    public void requestPermissions() {
        log.d("Request permissions called");
    }

    @Override
    public boolean isSupported() {
        log.d("isSupported called, returning true");
        return true;
    }

    @Override
    public void setAlwaysOn(boolean isAlwaysOn) {
        log.d("Setting alwaysOn to " + isAlwaysOn);
        this.isAlwaysOn = isAlwaysOn;
    }

    @Override
    public boolean isAlwaysOn() {
        log.d("isAlwaysOn called, returning " + isAlwaysOn);
        return isAlwaysOn;
    }
}
