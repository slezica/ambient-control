package io.slezica.ambientcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import io.slezica.ambientcontrol.utils.Ambient;

public class MainActivity extends AppCompatActivity {

    private Ambient ambient;

    private TextView permissionStateText;
    private TextView ambientStateText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ambient = new Ambient(this);

        permissionStateText = findViewById(R.id.permission_state);
        ambientStateText = findViewById(R.id.ambient_state);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (! ambient.isSupported()) {
            permissionStateText.setText("Not supported by system");
            ambientStateText.setText("");
            return;
        }

        if (ambient.hasPermissions()) {
            permissionStateText.setText("Permissions granted");
            ambientStateText.setText("Ambient always on: " + ambient.isAlwaysOn());

        } else {
            permissionStateText.setText("Permissions not granted");
            ambientStateText.setText("");
        }


    }
}
