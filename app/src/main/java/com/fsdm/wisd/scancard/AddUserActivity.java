package com.fsdm.wisd.scancard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity {

    EditText nom,uid;
    Button add,cancel;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

            nom = findViewById(R.id.user_name_field);
            uid = findViewById(R.id.user_uid);

            add=findViewById(R.id.bt_add);
            cancel=findViewById(R.id.bt_cancel);

        databaseHelper=new DatabaseHelper(this,DatabaseHelper.DATABASE_NAME,null,1);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nom.getText().toString();
                    String uuid = uid.getText().toString();
                    if(name.equals("")||uuid.equals(""))
                        Toast.makeText(AddUserActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    else {

                        databaseHelper.addNewUser(uuid,name);
                        Toast.makeText(AddUserActivity.this, "User has been added successfully", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


    }

