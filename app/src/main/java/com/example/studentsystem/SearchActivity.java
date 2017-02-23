package com.example.studentsystem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.studentsystem.MainActivity.db;

/**
 * Created by ljh on 2016/11/22.
 */
//搜索学生的活动
public class SearchActivity extends BaseActivity {
    private EditText searchEditText;
    private ListView searchListView;
    public static List<Student> searchStudentList;
    public static StudentAdapter searchStudentAdapter;
    private Button searchBackButton;
    private String searchWord;
    public static boolean search = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        //获得组件
        searchEditText= (EditText)  findViewById(R.id.search_edit_text);
        searchListView = (ListView) findViewById(R.id.search_list_view);
        searchBackButton = (Button) findViewById(R.id.search_back_button);
        search = true;  //标志是由SearchActivity进入其他活动的

        searchStudentList = new ArrayList<Student>();
        searchStudentAdapter = new StudentAdapter(this, R.layout.item, searchStudentList);
        searchListView.setAdapter(searchStudentAdapter);
        // 搜索框的键盘搜索键点击回调
        searchEditText.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    //获得输入的查询关键字
                    searchWord = searchEditText.getText().toString().trim();
                    //清空搜索框
                    searchEditText.setText("");
                    //清空学生列表并刷新
                    searchStudentList.clear();
                    searchStudentAdapter.notifyDataSetChanged();
                    //查询数据
                    Cursor cursor = db.query(
                            "Student", null, "id like ? or name like ? ",
                            new String[]{"%" + searchWord + "%" ,"%" + searchWord + "%"}, null, null , null);
                    if (cursor.moveToFirst()) {
                        do {
                            String id = cursor.getString(cursor.getColumnIndex("id"));
                            String name = cursor.getString(cursor.getColumnIndex("name"));
                            String cls = cursor.getString(cursor.getColumnIndex("cls"));
                            String addr = cursor.getString(cursor.getColumnIndex("addr"));
                            String phone = cursor.getString(cursor.getColumnIndex("phone"));
                            //将搜索结果写入列表并更新listview
                            searchStudentList.add(new Student(name, id, cls, addr, phone));
                            searchStudentAdapter.notifyDataSetChanged();
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    if (searchStudentList.size() == 0) {
                       Toast.makeText(SearchActivity.this, "没有相关数据", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        //退出活动则先更改标志再退出
        searchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search = false;
                onBackPressed();
            }
        });
    }

    //由其他活动启动该活动的接口
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }
}
