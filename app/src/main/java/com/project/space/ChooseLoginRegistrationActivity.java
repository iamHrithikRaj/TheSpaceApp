package com.project.space;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        Button mLogin = findViewById(R.id.login);
        Button mRegistration = findViewById(R.id.registration);
        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                return;

            }
        });
        mRegistration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplication(), RegistrationActivity.class);
                startActivity(intent);
                return;
            }
        });

    }
}
