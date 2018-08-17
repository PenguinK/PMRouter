package com.penguinmore.adapterannotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.penguinmore.pmannotation.Route;

@Route(path = "TestActivity")
public class TestActivity extends AppCompatActivity {

String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        id = getIntent().getBundleExtra("params").getString("id");
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
