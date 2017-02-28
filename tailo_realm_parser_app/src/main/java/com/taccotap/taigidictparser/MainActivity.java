package com.taccotap.taigidictparser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.taccotap.taigidictparser.tailo.parser.TlParseIntentService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TlParseIntentService.startParsing(this);
        finish();
    }
}
