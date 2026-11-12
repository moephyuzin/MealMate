package com.example.mealmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    Button btnSignup;
    DBHelper dbHelper;
    ArrayList<UserData> userDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignupActivity.this,R.color.blue));


        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnSignup = findViewById(R.id.btnSignup);
        dbHelper = new DBHelper(this);
        userDataArrayList = dbHelper.readUser();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill in all fields.",
                            Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    boolean userExists = false;

                    for (UserData user : userDataArrayList) {
                        if (username.equals(user.getUsername())) {
                            userExists = true;
                            break;
                        }
                    }

                    if (userExists) {
                        Toast.makeText(SignupActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.addUser(username, password, email);
                        Toast.makeText(SignupActivity.this, "Registration successful.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}