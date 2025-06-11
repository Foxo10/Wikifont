package com.example.wikifountains.activities;

import android.os.Bundle;

import com.example.wikifountains.R;

public class FountainMapsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentViewWithDrawer(R.layout.activity_fountain_maps);
    }
}