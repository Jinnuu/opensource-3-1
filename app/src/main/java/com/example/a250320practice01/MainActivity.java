package com.example.a250320practice01;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    String num1, num2;
    double sum=0;

    Button plusbtn,dividebtn,multiplybtn,minusbtn;
    EditText A,B;

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        plusbtn=(Button)findViewById(R.id.addButton);
        dividebtn=(Button)findViewById(R.id.divideButton);
        multiplybtn=(Button)findViewById(R.id.multiplyButton);
        minusbtn=(Button)findViewById(R.id.minusButton);

        A=(EditText)findViewById(R.id.A);
        B=(EditText) findViewById(R.id.B);

        result=(TextView)findViewById(R.id.result);

        plusbtn.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent arg1){
                num1=A.getText().toString();
                num2=B.getText().toString();
                sum=Double.parseDouble(num1)+Double.parseDouble(num2);
                result.setText("계산 결과: "+Double.toString(sum));
                return false;
            }
        });

        minusbtn.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent arg1){
                num1=A.getText().toString();
                num2=B.getText().toString();
                sum=Double.parseDouble(num1)-Double.parseDouble(num2);
                result.setText("계산 결과: "+Double.toString(sum));
                return false;
            }
        });

        multiplybtn.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent arg1){
                num1=A.getText().toString();
                num2=B.getText().toString();
                sum=Double.parseDouble(num1)*Double.parseDouble(num2);
                result.setText("계산 결과: "+Double.toString(sum));
                return false;
            }
        });

        dividebtn.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent arg1){
                num1=A.getText().toString();
                num2=B.getText().toString();
                sum=Double.parseDouble(num1)/Double.parseDouble(num2);
                result.setText("계산 결과: "+Double.toString(sum));
                return false;
            }
        });
    }
}