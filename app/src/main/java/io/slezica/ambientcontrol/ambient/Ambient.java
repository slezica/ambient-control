package io.slezica.ambientcontrol.ambient;

public interface Ambient {

    Ambient INSTANCE = null;

    boolean hasPermissions();

    void requestPermissions();

    boolean isSupported();

    void setAlwaysOn(boolean alwaysOn);

    boolean isAlwaysOn();
}
