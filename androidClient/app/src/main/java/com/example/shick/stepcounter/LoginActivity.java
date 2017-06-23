package com.example.shick.stepcounter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Created by zxy on 2017/6/14.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String REGUSTER = "register";
    private static final String LOGIN = "login";

    //DB
    private User_DB userdatabase;

    //views
    private Button loginbutton = null;
    private Button registerbutton = null;
    private EditText usernametext = null;
    private EditText passwordtext = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
                        //database version
                        NetSender netSender = new NetSender();
                        netSender.execute(REGUSTER,username, password);

                        /*if (!userdatabase.selectDB(username)) {
                           //userdatabase.insertDB(username, password);
                            //Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "该用户名已经被注册", Toast.LENGTH_SHORT).show();
                        }*/
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
                        /*if (!userdatabase.selectDB(username)) {
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
                        }*/

                        //database version
                        NetSender netSender = new NetSender();
                        netSender.execute(LOGIN, username, password);
                    }
                }
            }
        });
    }

    class NetSender extends AsyncTask<String, Void, JSONObject> {
        public String stringToMD5(String string) {
            byte[] hash;
            try {
                hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String type = params[0], username = params[1], password = stringToMD5(params[2]);
            String path = "http://10.0.2.2:3002/api/user/" + type;
            try {
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setUseCaches(false);

                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                printWriter.write(jsonObject.toString());
                printWriter.flush();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = bufferedReader.readLine();
                return  new JSONObject(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                if (jsonObject.getString("status").equals("login success")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("User", jsonObject.getString("content"));
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }
}
