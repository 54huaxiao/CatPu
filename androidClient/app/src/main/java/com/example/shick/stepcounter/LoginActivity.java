package com.example.shick.stepcounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by zxy on 2017/6/14.
 */

public class LoginActivity extends AppCompatActivity {

    //DB
    private User_DB userdatabase;

    //views
    private Button loginbutton = null;
    private Button registerbutton = null;
    private EditText usernametext = null;
    private EditText passwordtext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        loginbutton = (Button)findViewById(R.id.login_button);
        registerbutton = (Button)findViewById(R.id.register_button);
        usernametext = (EditText)findViewById(R.id.username);
        passwordtext = (EditText)findViewById(R.id.password);

        userdatabase = new User_DB(this, "UserDB", null, 1);

        //注册功能
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernametext.getText().toString();
                String password = passwordtext.getText().toString();

                if (username == null || username.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                } else {
                    if (password == null||password.equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                    } else {

                        if (!userdatabase.selectDB(username)) {
                           userdatabase.insertDB(username, password);
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "该用户名已经被注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //登录功能
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernametext.getText().toString();
                String password = passwordtext.getText().toString();

                if (username == null || username.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                } else {
                    if (password == null || password.equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!userdatabase.selectDB(username)) {
                            Toast.makeText(getApplicationContext(), "该用户未被注册！", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!userdatabase.getPassword(username).equals(password)) {
                                Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("User", username);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        });
    }
}
