
package com.example.studentsystem;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ljh on 2016/11/27.
 */

public class FeedbackActivity extends BaseActivity{
    private EditText feedbackEditText;
    private Button feedbackButton;
    private Toolbar feedbackToolbar;
    private String feedback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);

        feedbackToolbar = (Toolbar) findViewById(R.id.feedback_toolbar);
        feedbackEditText = (EditText) findViewById(R.id.feedback_edit_text);
        feedbackButton = (Button) findViewById(R.id.feedback_button);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                feedbackEditText.requestFocus();
            }
        }, 200);
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
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback = feedbackEditText.getText().toString();
                if (feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "请输入您的意见", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        post(feedback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Toast.makeText(FeedbackActivity.this, "感谢您的反馈", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }
    public static final MediaType MEDIA_TYPE_TEXT
            = MediaType.parse("text; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    public void post(String feedback) throws Exception {
        String postBody =feedback;

        Request request = new Request.Builder()
                .url("http://123.207.19.103/")
                .post(RequestBody.create(MEDIA_TYPE_TEXT, postBody))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Log.d("test", "response body----" + response.body().string());
    }
}