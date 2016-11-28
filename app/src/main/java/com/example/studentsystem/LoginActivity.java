package com.example.studentsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import java.util.TimerTask;

/**
 * Created by ljh on 2016/11/10.
 */

public class LoginActivity extends BaseActivity {
    private EditText accountEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPasswordCheckBox;
    public static String CHANGE_ACCOUNT = "" + false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK ) {
            ActivityCollector.finishAll();
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoged = preferences.getBoolean("isLoged", false);
        Intent changeIntent = getIntent();
        boolean changeAccount = Boolean.parseBoolean(changeIntent.getStringExtra(CHANGE_ACCOUNT));
        if (changeAccount) {
            editor = preferences.edit();
            editor.putBoolean("isLoged", false);
            editor.commit();
        } else if (isLoged) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        accountEditText = (EditText) findViewById(R.id.account_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        loginButton = (Button) findViewById(R.id.login_button);

        rememberPasswordCheckBox = (CheckBox) findViewById(R.id.remeber_password_checkbox);
        boolean isRemember = preferences.getBoolean("remember_password", false);

        if (isRemember) {
            String account = preferences.getString("account", "");
            String password = preferences.getString("password", "");
            accountEditText.setText(account);
            passwordEditText.setText(password);
            rememberPasswordCheckBox.setChecked(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if ("admin".equals(account) && "admin".equals(password)) {
                    editor = preferences.edit();
                    if (rememberPasswordCheckBox.isChecked()) {
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                        editor.putBoolean("isLoged", true);
                    } else {
                        editor.clear();
                    }
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "账号或密码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
