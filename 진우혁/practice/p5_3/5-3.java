package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        LinearLayout baseLayout =new LinearLayout(this);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        
        setContentView(baseLayout,params);
        EditText et=new EditText(this);
        et.setText("텍스트를 입력하세요.");
        baseLayout.addView(et);
        Button btn=new Button(this);
        btn.setText("버튼입니다.");
        btn.setBackgroundColor(Color.parseColor("#00FF00"));
        baseLayout.addView(btn);
        TextView text=new TextView(this);
        baseLayout.addView(text);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                text.setText(et.getText());
            }
        });
    }
}
