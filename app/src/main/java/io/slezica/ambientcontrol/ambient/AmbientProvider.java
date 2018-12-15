package io.slezica.ambientcontrol.ambient;

import android.content.Context;

public class AmbientProvider {

    public static final boolean DEBUG = false;


    public static Ambient getFor(Context context) {
        if (DEBUG) {
            return new AmbientMock(context);
        } else {
            return new AmbientImpl(context);
        }
    }

}
