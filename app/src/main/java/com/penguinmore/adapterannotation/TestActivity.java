package com.penguinmore.adapterannotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.penguinmore.pmannotation.NewIntent;

@NewIntent
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
