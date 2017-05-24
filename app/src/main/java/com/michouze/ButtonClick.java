package com.michouze;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ButtonClick extends AppCompatActivity {
    int clickcount=0;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_click);

        mButton = (Button)findViewById(R.id.clickme);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickcount = clickcount+1;
                if(clickcount==1){

                    Toast.makeText(getApplicationContext(),"Button Clicked firest time",Toast.LENGTH_LONG).show();
                }else{

                    Toast.makeText(getApplicationContext(),"Button clicked count is" + clickcount, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
