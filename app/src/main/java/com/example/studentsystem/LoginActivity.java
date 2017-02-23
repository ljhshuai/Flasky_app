package com.example.studentsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ljh on 2016/11/10.
 */
//登录活动
public class LoginActivity extends BaseActivity {
    private EditText accountEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPasswordCheckBox;
    public static String CHANGE_ACCOUNT = "" + false;   //默认不是从MainActivity退出来切换用户

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按返回键结束所有活动
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK ) {
            ActivityCollector.finishAll();
        }
        return true;
    }

    //验证账号密码的handler
    private Handler handler=new Handler(){
        public void handleMessage(Message m){
            String post_result = m.obj + "";  //得到线程传递的message
            if ("ok".equals(post_result)) {
                //验证服务器返回的结果

                editor = preferences.edit();    //编辑本地文件

                if (rememberPasswordCheckBox.isChecked()) {
                    //记住密码
                    String account = accountEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.putBoolean("isLoged", true);
                } else {
                    //未记住密码
                    editor.clear();
                }
                editor.commit();
                //进入MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if ("error".equals(post_result)){
                Toast.makeText(LoginActivity.this, "账号或密码错误",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "网络异常",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);  //设置加载布局

        preferences = PreferenceManager.getDefaultSharedPreferences(this);  //本地文件
        boolean isLoged = preferences.getBoolean("isLoged", false); //是否已登录
        //判断是从MainActivity退出还是直接启动app
        Intent changeIntent = getIntent();
        boolean changeAccount = Boolean.parseBoolean(changeIntent.getStringExtra(CHANGE_ACCOUNT));
        if (changeAccount) {
            //从MainActivity退出而来
            editor = preferences.edit();
            editor.putBoolean("isLoged", false);
            editor.commit();
        } else if (isLoged) {
            //如果已登录则不需再显示登录页面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        //获得输入框和按钮
        accountEditText = (EditText) findViewById(R.id.account_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        loginButton = (Button) findViewById(R.id.login_button);
        signupButton = (Button) findViewById(R.id.sign_up_button);
        //获得记住密码复选框
        rememberPasswordCheckBox = (CheckBox) findViewById(R.id.remeber_password_checkbox);
        //判断是否记住密码
        boolean isRemember = preferences.getBoolean("remember_password", false);

        if (isRemember) {
            //记住密码则将账户密码从文件中读出写入编辑框
            String account = preferences.getString("account", "");
            String password = preferences.getString("password", "");
            accountEditText.setText(account);
            passwordEditText.setText(password);
            rememberPasswordCheckBox.setChecked(true);
        }
        //登录按钮监听事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得试图登录的账户
                String account = accountEditText.getText().toString();
                editor = preferences.edit();    //编辑本地文件
                editor.putString("account", account);
                editor.commit();
                //创建新的线程提交账户密码
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获得账户密码
                        String account = accountEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        //将账户密码放入参数中已发送给服务器
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("account", account);
                        params.put("password", password);
                        //使用Message封装非UI线程的消息
                        Message m=new Message();

                        try {
                            //使用message的参数来获得服务器返回的数据
                            URL url = new URL("http://www.hustljh.cn/android/login");
                            m.obj = HttpUtils.submitPostData(params, "utf-8", url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        //使用Handler发送消息
                        handler.sendMessage(m);
                    }
                }) .start();
            }
        });
        //简单地将注册活动发往web，后续可更改
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse("http://123.207.19.103/signup");
                intent1.setData(url);
                startActivity(intent1);
            }
        });

    }

}
