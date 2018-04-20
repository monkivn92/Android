package com.vdroid.transitionlikeios;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_slide).setOnClickListener(this);
        findViewById(R.id.button_fade).setOnClickListener(this);
        findViewById(R.id.button_default).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        Intent intent = new Intent(this, ImageActivity.class);
        switch (view.getId())
        {
            case R.id.button_slide:
                intent.putExtra("type", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
            case R.id.button_fade:
                intent.putExtra("type", 2);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                startActivity(intent);
                break;
        }
    }
}

