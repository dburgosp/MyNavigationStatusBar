package com.davidburgosprieto.mynavigationstatusbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    /* ************************ */
    /* Private member variables */
    /* ************************ */

    private NavigationStatusBar myNavigationStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myNavigationStatusBar = findViewById(R.id.custom_view);
    }
}
