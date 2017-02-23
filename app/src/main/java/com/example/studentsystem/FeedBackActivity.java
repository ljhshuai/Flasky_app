
package com.example.studentsystem;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.studentsystem.MainActivity.db;
import static com.example.studentsystem.MainActivity.studentAdapter;
import static com.example.studentsystem.MainActivity.studentList;
import static com.example.studentsystem.SearchActivity.search;
import static com.example.studentsystem.SearchActivity.searchStudentAdapter;
import static com.example.studentsystem.SearchActivity.searchStudentList;

/**
 * Created by ljh on 2016/11/27.
 */
//反馈页面的活动
public class FeedbackActivity extends BaseActivity{
    private EditText feedbackEditText;
    private Button feedbackButton;
    private Toolbar feedbackToolbar;
    private String feedback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);

        //获得组件
        feedbackToolbar = (Toolbar) findViewById(R.id.feedback_toolbar);
        feedbackEditText = (EditText) findViewById(R.id.feedback_edit_text);
        feedbackButton = (Button) findViewById(R.id.feedback_button);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                feedbackEditText.requestFocus();
            }
        }, 200);
        //工具栏的设置
        feedbackToolbar.setTitle("反馈");
        setSupportActionBar(feedbackToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        feedbackToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);  //本地文件
        //反馈提交按钮监听事件
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback = feedbackEditText.getText().toString();   //反馈信息

                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "请输入您的意见", Toast.LENGTH_SHORT).show();
                } else {
                    //创建新的线程提交意见反馈
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String account = preferences.getString("account", "");  //获得当前保存的用户名
                            //将账户密码放入参数中已发送给服务器
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("message", feedback);     //反馈信息放入请求参数中
                            params.put("account", account);

                            //使用Message封装非UI线程的消息
                            Message m=new Message();

                            try {
                                //使用message的参数来获得服务器返回的数据
                                URL url = new URL("http://www.hustljh.cn/android/feedback");
                                HttpUtils.submitPostData(params, "utf-8", url);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }) .start();

                    Toast.makeText(FeedbackActivity.this, "感谢您的反馈", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}