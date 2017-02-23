package com.example.studentsystem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import static com.example.studentsystem.MainActivity.*;
import static com.example.studentsystem.SearchActivity.*;




/**
 * Created by ljh on 2016/11/21.
 */
//显示学生信息详情的activity
public class ItemActivity extends BaseActivity {
    private Toolbar itemToolbar;
    private Button completButton;
    private Button editButton;
    //学生信息编辑框
    private EditText nameEditText;
    private EditText idEditText;
    private EditText clsEditText;
    private EditText addrEditText;
    private EditText phoneEditText;
    public static String EXTRA_DATA;    //根据是否有额外信息来确定是新建还是显示详情
    private boolean update = false; //默认不是需要修改，即只是新建
    private String primaryKey;  //由数据库查找学生所需的主键
    private int position;   //由列表显示学生所需的postition
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_layout);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);  //本地文件
        //工具条的相关设置
        itemToolbar = (Toolbar) findViewById(R.id.item_toolbar);
        itemToolbar.setTitle("新建"); //默认是新建
        setSupportActionBar(itemToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        itemToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //获得所需组件
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        idEditText = (EditText) findViewById(R.id.id_edit_text);
        clsEditText = (EditText) findViewById(R.id.cls_edit_text);
        addrEditText = (EditText) findViewById(R.id.addr_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        completButton = (Button) findViewById(R.id.complete_button);
        editButton = (Button) findViewById(R.id.edit_button);


        if (getIntent().getStringExtra(EXTRA_DATA) != null) {
            //有额外信息说明不是新建，只需显示详情
            itemToolbar.setTitle("详情");
            completButton.setVisibility(View.GONE); //不显示确定按钮
            position = Integer.parseInt(getIntent().getStringExtra(EXTRA_DATA));
            if(search) {
                //从SearchActivity而来，根据SearchActivity传来的position来显示
                primaryKey = searchStudentList.get(position).getId();
                //显示详情则需将学生信息填入编辑框
                nameEditText.setText(searchStudentList.get(position).getName());
                idEditText.setText(searchStudentList.get(position).getId());
                clsEditText.setText(searchStudentList.get(position).getCls());
                addrEditText.setText(searchStudentList.get(position).getAddr());
                phoneEditText.setText(searchStudentList.get(position).getPhone());
            } else {
                //从Activity而来，根据MainActivity传来的position来显示
                primaryKey = studentList.get(position).getId();
                nameEditText.setText(studentList.get(position).getName());
                idEditText.setText(studentList.get(position).getId());
                clsEditText.setText(studentList.get(position).getCls());
                addrEditText.setText(studentList.get(position).getAddr());
                phoneEditText.setText(studentList.get(position).getPhone());
            }
            //显示详情则编辑框不能编辑
            nameEditText.setFocusable(false);
            idEditText.setFocusable(false);
            clsEditText.setFocusable(false);
            addrEditText.setFocusable(false);
            phoneEditText.setFocusable(false);

            //将学号存储文件中以备修改使用
            editor = preferences.edit();
            editor.putString("old_id", idEditText.getText().toString());
            editor.commit();

        } else {
            //是新建则不显示修改按钮
            editButton.setVisibility(View.GONE);
            //新建则修改标志为false
            update = false;
        }

        //修改按钮点击时的监听事件
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示确定按钮
                completButton.setVisibility(View.VISIBLE);
                update = true;  //修改标志为true

                //编辑框可获得焦点且姓名栏自动获取焦点
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.requestFocusFromTouch();
                idEditText.setFocusableInTouchMode(true);
                clsEditText.setFocusableInTouchMode(true);
                addrEditText.setFocusableInTouchMode(true);
                phoneEditText.setFocusableInTouchMode(true);
            }
        });
        //确定按钮设置监听事件
        completButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String name = nameEditText.getText().toString();
                 String id = idEditText.getText().toString();
                 String cls = clsEditText.getText().toString();
                 String addr = addrEditText.getText().toString();
                 String phone = phoneEditText.getText().toString();

                 if (name.isEmpty() || id.isEmpty() || cls.isEmpty() || addr.isEmpty() || phone.isEmpty()) {
                     Toast.makeText(ItemActivity.this, "输入不能为空",
                             Toast.LENGTH_SHORT).show();
                 } else {
                     //实例化student
                     Student student = new Student(name, id, cls, addr, phone);
                     serverChangeStudent(student);
                 }
             }
         });
    }

    //修改学生信息的handler
    private Handler handler=new Handler(){
        public void handleMessage(Message m){
            String post_result = m.obj + "";  //得到线程传递的message
            if ("ok".equals(post_result)) {
                String name = nameEditText.getText().toString();
                String id = idEditText.getText().toString();
                String cls = clsEditText.getText().toString();
                String addr = addrEditText.getText().toString();
                String phone = phoneEditText.getText().toString();

                //创建可向数据库添加的信息
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("id", id);
                values.put("cls", cls);
                values.put("addr", addr);
                values.put("phone", phone);
                if (update) {
                    //是修改
                    if (search) {
                        //是从SearchActivity进来进行的修改
                        searchStudentList.set(position, new Student(name, id, cls, addr, phone));
                        searchStudentAdapter.notifyDataSetChanged();
                    } else {
                        //是从MainActivity进来进行的修改
                        studentList.set(position, new Student(name, id, cls, addr, phone));
                        studentAdapter.notifyDataSetChanged();
                    }
                    db.update("Student", values, "id = ?", new String[]{primaryKey});
                } else {
                    //是新建
                    studentList.add(new Student(name, id, cls, addr, phone));
                    db.insert("Student", null, values);
                }
                values.clear();
                //确定后返回
                onBackPressed();
                Toast.makeText(ItemActivity.this, "操作成功",
                        Toast.LENGTH_SHORT).show();
            } else if ("error".equals(post_result)){
                Toast.makeText(ItemActivity.this, "发生错误",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ItemActivity.this, "网络异常",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    //服务器端修改学生信息
    private void serverChangeStudent(final Student student) {
        //创建新的线程提交更改
        new Thread(new Runnable() {
            @Override
            public void run() {
                String old_id = "";
                old_id = preferences.getString("old_id", "");
                String account = preferences.getString("account", "");  //获得当前保存的用户名
                //将账户密码放入参数中已发送给服务器
                Map<String, String> params = new HashMap<String, String>();
                params.put("old_id", old_id);
                params.put("account", account);
                params.put("id", student.getId());
                params.put("name", student.getName());
                params.put("cls", student.getCls());
                params.put("addr", student.getAddr());
                params.put("phone", student.getPhone());

                //使用Message封装非UI线程的消息
                Message m=new Message();

                try {
                    //使用message的参数来获得服务器返回的数据
                    URL url = new URL("http://www.hustljh.cn/android/change");
                    m.obj = HttpUtils.submitPostData(params, "utf-8", url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //使用Handler发送消息
                handler.sendMessage(m);
            }
        }) .start();
    }

    //提供给新建按钮启动该活动的接口
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ItemActivity.class);
        context.startActivity(intent);
    }
    //提供给MainActivity和SearchActivity的接口，可以通过EXTRA_DATA来传递额外信息
    public static void actionStart(Context context, String position) {
        Intent intent = new Intent(context, ItemActivity.class);
        intent.putExtra(EXTRA_DATA, position);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        //将文件中的学号情况以防后续影响
        editor = preferences.edit();
        editor.putString("old_id", "");
        editor.commit();
        super.onDestroy();
    }
}


