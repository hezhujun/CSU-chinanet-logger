package com.hezhujun.csu_chinanet_logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import login.Login;
import login.LoginResponseType;
import login.Logout;
import login.LogoutResponseType;

public class MainActivity extends AppCompatActivity {

    private EditText accountEditText;
    private EditText passwordEditText;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    LoginResponseType loginResponse = (LoginResponseType) msg.obj;
                    Log.i("loginResponse", LoginResponseType.parse(loginResponse));
                    Toast.makeText(MainActivity.this, LoginResponseType.parse(loginResponse), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    LogoutResponseType logoutResponse = (LogoutResponseType) msg.obj;
                    Log.i("loginResponse", LogoutResponseType.parse(logoutResponse));
                    Toast.makeText(MainActivity.this, LogoutResponseType.parse(logoutResponse), Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Exception e = (Exception) msg.obj;
                    Log.i("error", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private PropertyUtilAndroidImpl propertyUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("数字中南登录器");

        propertyUtil = new PropertyUtilAndroidImpl(getSharedPreferences("chinanetlogin", Context.MODE_PRIVATE));

        accountEditText = (EditText) findViewById(R.id.account);
        passwordEditText = (EditText) findViewById(R.id.password);

        Map<String, String> map = readLoginInfo();
        accountEditText.setText(map.get("account"));
        passwordEditText.setText(map.get("password"));

        Button loginButton = (Button) findViewById(R.id.login);
        Button logoutButton = (Button) findViewById(R.id.logout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = accountEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (account == null || account.trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "请输入账户", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password == null || account.trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveLoginInfo(account, password);

                Map<String, String> map = readLoginInfo();
                if (account.equals(map.get("account")) && password.equals(map.get("password"))) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Login login = new Login(propertyUtil);
                                LoginResponseType response = login.login();
                                Message message = new Message();
                                message.what = 0;
                                message.obj = response;
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                Message message = new Message();
                                message.what = -1;
                                message.obj = e;
                                handler.sendMessage(message);
                            }
                        }
                    }.start();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Login login = new Login(propertyUtil);
                                LoginResponseType response = login.loginAndCache(account, password);
                                Message message = new Message();
                                message.what = 0;
                                message.obj = response;
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                Message message = new Message();
                                message.what = -1;
                                message.obj = e;
                                handler.sendMessage(message);
                            }
                        }
                    }.start();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Logout logout = new Logout(propertyUtil);
                            LogoutResponseType response = logout.logout();
                            Message message = new Message();
                            message.what = 1;
                            message.obj = response;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            Message message = new Message();
                            message.what = -1;
                            message.obj = e;
                            handler.sendMessage(message);
                        }
                    }
                }.start();
            }
        });
    }

    private Map<String, String> readLoginInfo() {
        SharedPreferences sp = getSharedPreferences("chinanetlogger", Context.MODE_PRIVATE);
        Map<String, String> map = new HashMap<String, String>();
        map.put("account", sp.getString("account", null));
        map.put("password", sp.getString("password", null));
        return map;
    }

    private void saveLoginInfo(String account, String password) {
        SharedPreferences sp = getSharedPreferences("chinanetlogger", Context.MODE_PRIVATE);
        sp.edit().putString("account", account).putString("password", password).apply();
    }

}
