package com.example.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class fragment_settings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings); // XML 파일명과 동일하게

        Button btnMyProfile = findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // body_setting Activity로 전환
                Intent intent = new Intent(fragment_settings.this,Body_Settings.class);
                startActivity(intent);
            }
        });
    }
}
