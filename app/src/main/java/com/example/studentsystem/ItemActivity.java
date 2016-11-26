package com.example.studentsystem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static com.example.studentsystem.MainActivity.*;
import static com.example.studentsystem.SearchActivity.search;
import static com.example.studentsystem.SearchActivity.searchStudentAdapter;
import static com.example.studentsystem.SearchActivity.searchStudentList;



/**
 * Created by ljh on 2016/11/21.
 */

public class ItemActivity extends BaseActivity {
    private Toolbar itemToolbar;
    private Button completButton;
    private Button editButton;
    private EditText nameEditText;
    private EditText idEditText;
    private EditText sexEditText;
    private EditText ageEditText;
    private EditText scoreEditText;
    public static String EXTRA_DATA;
    private boolean update = false;
    private String primaryKey;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_layout);

        itemToolbar = (Toolbar) findViewById(R.id.item_toolbar);
        itemToolbar.setTitle("新建");

        setSupportActionBar(itemToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        itemToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        idEditText = (EditText) findViewById(R.id.id_edit_text);
        sexEditText = (EditText) findViewById(R.id.sex_edit_text);
        ageEditText = (EditText) findViewById(R.id.age_edit_text);
        scoreEditText = (EditText) findViewById(R.id.score_edit_text);
        completButton = (Button) findViewById(R.id.complete_button);
        editButton = (Button) findViewById(R.id.edit_button);

        if (getIntent().getStringExtra(EXTRA_DATA) != null) {
            itemToolbar.setTitle("详情");
            completButton.setVisibility(View.GONE);
            position = Integer.parseInt(getIntent().getStringExtra(EXTRA_DATA));
           if(search) {
                primaryKey = searchStudentList.get(position).getId();
                nameEditText.setText(searchStudentList.get(position).getName());
                idEditText.setText(searchStudentList.get(position).getId());
                sexEditText.setText(searchStudentList.get(position).getSex());
                ageEditText.setText("" + searchStudentList.get(position).getAge());
                scoreEditText.setText("" + searchStudentList.get(position).getScore());
            } else {
                primaryKey = studentList.get(position).getId();
                nameEditText.setText(studentList.get(position).getName());
                idEditText.setText(studentList.get(position).getId());
                sexEditText.setText(studentList.get(position).getSex());
                ageEditText.setText("" + studentList.get(position).getAge());
                scoreEditText.setText("" + studentList.get(position).getScore());
            }
            nameEditText.setFocusable(false);
            idEditText.setFocusable(false);
            sexEditText.setFocusable(false);
            ageEditText.setFocusable(false);
            scoreEditText.setFocusable(false);

        } else {
            editButton.setVisibility(View.GONE);
            update = false;
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completButton.setVisibility(View.VISIBLE);
                update = true;
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.requestFocusFromTouch();
                idEditText.setFocusableInTouchMode(true);
                sexEditText.setFocusableInTouchMode(true);
                ageEditText.setFocusableInTouchMode(true);
                scoreEditText.setFocusableInTouchMode(true);
            }
        });
        completButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "11111");
                String name = nameEditText.getText().toString();
                String id = idEditText.getText().toString();
                Log.d("test", "5555");
                String sex = sexEditText.getText().toString();
                Log.d("test", "666");
                int age = Integer.parseInt(ageEditText.getText().toString());
                Log.d("test", "777");
                float score = Float.parseFloat(scoreEditText.getText().toString());

                Log.d("test", "333331");
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("id", id);
                values.put("sex", sex);
                values.put("age", age);
                values.put("score", score);
                Log.d("test", "33322112");
                if (update) {
                    Log.d("test", "1w2221");
                    if(search) {
                        searchStudentList.set(position, new Student(name, id, sex, age, score));
                        searchStudentAdapter.notifyDataSetChanged();
                    } else {
                        studentList.set(position, new Student(name, id, sex, age ,score));
                        studentAdapter.notifyDataSetChanged();
                    }
                    db.update("Student", values, "id = ?", new String[] {primaryKey});
                } else {
                    Log.d("test", "add");
                    studentList.add(new Student(name, id, sex, age ,score));
                    db.insert("Student", null, values);
                }
                values.clear();

                onBackPressed();
            }
        });

    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ItemActivity.class);
        context.startActivity(intent);
    }
    public static void actionStart(Context context, String position) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.putExtra(EXTRA_DATA, position);
        context.startActivity(intent);
    }
}
