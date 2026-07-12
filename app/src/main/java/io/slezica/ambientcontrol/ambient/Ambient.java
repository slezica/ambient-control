package io.slezica.ambientcontrol.ambient;

import java.util.List;

public interface Ambient {

    List<StatusItem> getStatus();

    boolean hasPermissions();

    void requestPermissions();

    boolean isSupported();

    void setAlwaysOn(boolean alwaysOn);

    boolean isAlwaysOn();
}
