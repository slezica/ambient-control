package io.slezica.ambientcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import io.slezica.ambientcontrol.ambient.Ambient;
import io.slezica.ambientcontrol.ambient.AmbientImpl;
import io.slezica.ambientcontrol.ambient.AmbientProvider;
import io.slezica.ambientcontrol.utils.PowerUtils;

public class MainActivity extends AppCompatActivity {

    private Ambient ambient;

    private TextView explanation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ambient = AmbientProvider.getFor(this);

        explanation = (TextView) findViewById(R.id.explanation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!ambient.isSupported()) {
            explanation.setText(getString(R.string.not_supported));

        } else if (!ambient.hasPermissions()) {
            explanation.setText(getString(R.string.no_permissions));

        } else {
            explanation.setText(getString(
                    R.string.ambient_state,
                    ambient.isAlwaysOn() ? "ON" : "OFF"
            ));
        }


    }
}
