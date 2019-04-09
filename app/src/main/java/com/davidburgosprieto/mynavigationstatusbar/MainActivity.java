package com.davidburgosprieto.mynavigationstatusbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity
        extends AppCompatActivity
        implements NavigationStatusBar.OnInteractionListener {

    /* ************************ */
    /* Private member variables */
    /* ************************ */

    private NavigationStatusBar myNavigationStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myNavigationStatusBar = findViewById(R.id.custom_view);
        myNavigationStatusBar.attachListener(this);
    }

    @Override
    public void onInteraction(int buttonIndex) {
        Toast.makeText(this, "Clicked " + buttonIndex, Toast.LENGTH_SHORT).show();
    }
}
