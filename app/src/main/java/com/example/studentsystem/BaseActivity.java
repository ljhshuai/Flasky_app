package com.example.studentsystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ljh on 2016/11/26.
 */

//管理活动的基础活动
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
