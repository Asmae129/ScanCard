package com.fsdm.wisd.scancard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText username,pass;
    ImageButton login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=findViewById(R.id.username);
        pass=findViewById(R.id.pass);
        login=findViewById(R.id.loginb);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String passw = pass.getText().toString();
                if(user.equals("")||pass.equals(""))
                    Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    boolean checkuserpass=(user.equals("admin") && passw.equals("admin"));
                    if(checkuserpass){
                        Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


}
