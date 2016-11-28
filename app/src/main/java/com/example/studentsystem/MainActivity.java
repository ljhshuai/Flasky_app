package com.example.studentsystem;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.studentsystem.LoginActivity.CHANGE_ACCOUNT;


/**
 * Created by ljh on 2016/11/18.
 */

public class MainActivity extends BaseActivity {

    public static List<Student> studentList = new ArrayList<Student>();
    public static ListView listView;
    public static NavigationView navigationView;
    public static DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private View header;
    private Button addButton;
    private MyDatabaseHelper myDatabaseHelper;
    public static SQLiteDatabase db;
    public static StudentAdapter studentAdapter;
    int touchSlop = 10;
    private ImageButton goSearchImageButton;
    private static Boolean isQuit = false;

    @Override
    protected void onRestart() {
        super.onRestart();
        initStudents();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.list_view);
        addButton = (Button) findViewById(R.id.add_button);
        goSearchImageButton = (ImageButton) findViewById(R.id.go_search_image_button);
        myDatabaseHelper = new MyDatabaseHelper(this, "StudentSystem.db", null, 1);

        navigationView.setItemIconTintList(null);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);//为抽屉滑出的监听添加触发器
        toggle.syncState();//加上同步


        studentAdapter = new StudentAdapter(
                this, R.layout.item, studentList);
        listView.setAdapter(studentAdapter);
        initStudents();
        header = new View(MainActivity.this);
        header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.abc_action_bar_default_height_material)));
        header.setBackgroundColor(Color.parseColor("#00000000"));
        listView.addHeaderView(header);
        listView.setOnTouchListener(onTouchListener);
        addButton.setOnClickListener(onClickListener);

        goSearchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.actionStart(MainActivity.this);
            }
        });

        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.head_view).setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "点击了头布局(图片)",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //导航页里菜单项的单击事件
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu1:
                                Intent intent1 = new Intent();
                                intent1.setAction("android.intent.action.VIEW");
                                Uri url = Uri.parse("http://123.207.19.103/");
                                intent1.setData(url);
                                startActivity(intent1);
                                break;
                            case R.id.menu2:
                                Intent intent2 = new Intent(MainActivity.this, FeedbackActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.menu3:
                                Intent intent3 = new Intent();
                                intent3.setAction("android.intent.action.VIEW");
                                Uri url2 = Uri.parse("https://github.com/huster1446/StudentSystem");
                                intent3.setData(url2);
                                startActivity(intent3);
                                break;
                            case R.id.menu4:
                                Intent intent4 = new Intent(MainActivity.this, LoginActivity.class);
                                intent4.putExtra(CHANGE_ACCOUNT, "" + true);
                                startActivity(intent4);
                            default:
                                break;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }



    private void initStudents() {
        db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Student", null, null, null, null, null, null);
        studentList.clear();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String sex = cursor.getString(cursor.getColumnIndex("sex"));
                int age = cursor.getInt(cursor.getColumnIndex("age"));
                float score = cursor.getFloat(cursor.getColumnIndex("score"));
                studentList.add(new Student(name, id, sex, age, score));
            } while (cursor.moveToNext());
        }
        studentAdapter.notifyDataSetChanged();
        cursor.close();
        if (studentList.size() == 0) {
            Toast.makeText(MainActivity.this, "数据库无数据", Toast.LENGTH_SHORT).show();
        }
    }



    AnimatorSet backAnimatorSet;//这是显示头尾元素使用的动画

    private void animateBack() {
        //先清除其他动画
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            hideAnimatorSet.cancel();
        }
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            backAnimatorSet = new AnimatorSet();
            //下面两句是将头尾元素放回初始位置。
            ObjectAnimator headerAnimator = ObjectAnimator.ofFloat
                    (toolbar, "translationY", toolbar.getTranslationY(), 0f);
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(headerAnimator);
            backAnimatorSet.setDuration(300);
            backAnimatorSet.playTogether(animators);
            backAnimatorSet.start();
        }
    }

    AnimatorSet hideAnimatorSet;//这是隐藏头尾元素使用的动画

    private void animateHide() {
        //先清除其他动画
        if (backAnimatorSet != null && backAnimatorSet.isRunning()) {
            backAnimatorSet.cancel();
        }
        if (hideAnimatorSet != null && hideAnimatorSet.isRunning()) {
            //如果这个动画已经在运行了，就不管它
        } else {
            hideAnimatorSet = new AnimatorSet();
            ObjectAnimator headerAnimator = ObjectAnimator.ofFloat
                    (toolbar, "translationY", toolbar.getTranslationY(), -toolbar.getHeight());//将toolbartoolbar隐藏到上面
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(headerAnimator);
            hideAnimatorSet.setDuration(200);
            hideAnimatorSet.playTogether(animators);
            hideAnimatorSet.start();
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemActivity.actionStart(MainActivity.this);
        }
    };
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastY = 0f;
        float currentY = 0f;
        //下面两个表示滑动的方向，大于0表示向下滑动，小于0表示向上滑动，等于0表示未滑动
        int lastDirection = 0;
        int currentDirection = 0;
        float down_x = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    down_x = event.getX();
                    Log.d("test", "down_x  " + down_x);
                    lastY = event.getY();
                    currentY = event.getY();
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (listView.getFirstVisiblePosition() > 0) {
                        float tmpCurrentY = event.getY();
                        if (Math.abs(tmpCurrentY - lastY) > touchSlop) {//滑动距离大于touchslop时才进行判断
                            currentY = tmpCurrentY;
                            currentDirection = (int) (currentY - lastY);
                            if (lastDirection != currentDirection) {

                                //如果与上次方向不同，则执行显/隐动画
                                if (currentDirection < 0) {
                                    animateHide();
                                } else {
                                    animateBack();
                                }
                            }
                            lastY = currentY;
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //手指抬起的时候要把currentDirection设置为0，这样下次不管向哪拉，都与当前的不同（其实在ACTION_DOWN里写了之后这里就用不着了……）
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
            }
            return false;
        }
    };

    Timer timer = new Timer();
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                ActivityCollector.finishAll();
            }
        }
        return true;
    }
}

