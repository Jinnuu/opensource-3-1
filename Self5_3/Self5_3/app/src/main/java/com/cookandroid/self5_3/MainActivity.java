package com.cookandroid.self5_3;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout baseLayout = new LinearLayout(this);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(baseLayout, params);

        EditText ed = new EditText(this);
        ed.setHint("여기에 입력하세요");

        baseLayout.addView(ed);

        Button btn = new Button(this);
        btn.setText("버튼입니다");
        btn.setBackgroundColor(Color.parseColor("#FFFF00"));
        baseLayout.addView(btn);

        TextView tv = new TextView(this);
        tv.setText("텍스트뷰입니다.");
        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#FF00FF"));
        baseLayout.addView(tv);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                tv.setText(ed.getText().toString());
            }
        });

    }

}
