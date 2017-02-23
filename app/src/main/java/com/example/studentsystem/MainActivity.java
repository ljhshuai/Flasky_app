package com.example.studentsystem;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.studentsystem.LoginActivity.CHANGE_ACCOUNT;


public class MainActivity extends BaseActivity {
    //定义组件实例
    public static List<Student> studentList = new ArrayList<Student>(); //学生列表
    public static ListView listView;    //显示学生的listview
    public static NavigationView navigationView;    //navigation
    public static DrawerLayout drawerLayout;    //Navigation中的抽屉
    private Toolbar toolbar;    //工具条
    private View header;    //listview的header
    private Button addButton;   //新建学生按钮
    private MyDatabaseHelper myDatabaseHelper;  //数据库管理类
    public static SQLiteDatabase db;    //数据库实例
    private SharedPreferences preferences;  //保存用户账号的文件
    public static StudentAdapter studentAdapter;    //学生适配类
    int touchSlop = 10; //判断滑动距离是否满足的标准
    private ImageButton goSearchImageButton;    //查找学生图标按钮
    private static Boolean isQuit = false;  //用于控制双击退出判断

    @Override
    protected void onRestart() {
        super.onRestart();
        initStudents();     //每次激活主活动会刷新学生列表
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //设置加载布局

        //实例化布局中的组件
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.list_view);
        addButton = (Button) findViewById(R.id.add_button);
        goSearchImageButton = (ImageButton) findViewById(R.id.go_search_image_button);
        //实例化数据库Helper
        myDatabaseHelper = new MyDatabaseHelper(this, "StudentSystem.db", null, 1);


        navigationView.setItemIconTintList(null);   //忘了是什么了
        toolbar.setTitle("");   //工具栏不显示标题
        setSupportActionBar(toolbar);   //应用toolbar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置toolbar显示返回键
        getSupportActionBar().setHomeButtonEnabled(true);   //设置返回键有效

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);    //将toolbar点击设为触发器
        drawerLayout.addDrawerListener(toggle); //为抽屉滑出的监听添加触发器
        toggle.syncState(); //加上同步


        studentAdapter = new StudentAdapter(this, R.layout.item, studentList);  //为studentAdapter赋值
        listView.setAdapter(studentAdapter);    //设置listview的适配器
        initStudents();     //初始化学生列表
        header = new View(MainActivity.this);   //实例化header
        header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.abc_action_bar_default_height_material)));    //设置header参数
        header.setBackgroundColor(Color.parseColor("#00000000"));   //设置背景颜色
        listView.addHeaderView(header);     //为listview添加header
        listView.setOnTouchListener(onTouchListener);   //为listview添加监听事件
        addButton.setOnClickListener(onClickListener);  //为新建按钮添加监听事件
        //为查找按钮添加监听事件
        goSearchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.actionStart(MainActivity.this);
            }
        });

        //获取Navigation的头部视图并添加监听事件，后续扩展功能可能会用到
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.head_view).setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "这里以后会有用的",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //导航页里菜单项的单击事件
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            //导航到项目网页首页
                            case R.id.menu1:
                                Intent intent1 = new Intent();
                                intent1.setAction("android.intent.action.VIEW");
                                Uri url = Uri.parse("http://www.hustljh.cn/");
                                intent1.setData(url);
                                startActivity(intent1);
                                break;
                            //启动反馈activity
                            case R.id.menu2:
                                Intent intent2 = new Intent(MainActivity.this, FeedbackActivity.class);
                                startActivity(intent2);
                                break;
                            //链接到项目github
                            case R.id.menu3:
                                Intent intent3 = new Intent();
                                intent3.setAction("android.intent.action.VIEW");
                                Uri url2 = Uri.parse("https://github.com/huster1446/StudentSystem");
                                intent3.setData(url2);
                                startActivity(intent3);
                                break;
                            //退出到登录界面
                            case R.id.menu4:
                                Intent intent4 = new Intent(MainActivity.this, LoginActivity.class);
                                intent4.putExtra(CHANGE_ACCOUNT, "" + true);
                                startActivity(intent4);
                            default:
                                break;
                        }
                        //关闭抽屉
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    //从服务器加载数据handler
    private Handler handler=new Handler(){
        public void handleMessage(Message m){
            //清空列表已有项
            studentList.clear();
            if (m.obj.toString() == "") {
                Toast.makeText(MainActivity.this, "网络异常, 加载本地数据库",
                        Toast.LENGTH_SHORT).show();
                //得到数据库实例
                db = myDatabaseHelper.getReadableDatabase();
                //查找所有学生
                Cursor cursor = db.query("Student", null, null, null, null, null, null);
                //将数据库中的学生添加进学生列表
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex("id"));
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String cls = cursor.getString(cursor.getColumnIndex("cls"));
                        String addr = cursor.getString(cursor.getColumnIndex("addr"));
                        String phone = cursor.getString(cursor.getColumnIndex("phone"));
                        studentList.add(new Student(name, id, cls, addr, phone));
                    } while (cursor.moveToNext());
                }
                //更新listview
                studentAdapter.notifyDataSetChanged();
                //关闭数据库游标
                cursor.close();
            }  else if (!m.obj.toString().equals("error")){
                //对返回的数据解析得到学生信息
                String[] return_info = m.obj.toString().split(" ");
                ContentValues values = new ContentValues();
                db = myDatabaseHelper.getReadableDatabase();
                for (int i = 0; i < return_info.length; i += 5) {
                    //获得学生信息
                    String name = return_info[i];
                    String id = return_info[i+1];
                    String cls = return_info[i+2];
                    String addr = return_info[i+3];
                    String phone = return_info[i+4];

                    //加入学生列表
                    studentList.add(new Student(name, id, cls, addr, phone));

                    //创建可向数据库添加的信息
                    values.put("name", name);
                    values.put("id", id);
                    values.put("cls", cls);
                    values.put("addr", addr);
                    values.put("phone", phone);
                    //加入数据库
                    db.insert("Student", null, values);
                    values.clear();

                  //更新listview
                    studentAdapter.notifyDataSetChanged();
                }
            }
            //数据库中无数据
            if (studentList.size() == 0) {
                Toast.makeText(MainActivity.this, "数据库无数据", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //初始化学生列表方法
    private void initStudents() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);  //本地文件
        final String account = preferences.getString("account", "");  //获得当前保存的用户名
        //创建线程来向服务器请求数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("account", account);
                //使用Message封装非UI线程的消息
                Message m=new Message();
                m.obj = null;
                try {
                    //将返回的数据保存在m.obj中传递给handler
                    URL url = new URL("http://www.hustljh.cn/android/init");
                    m.obj = HttpUtils.submitPostData(params, "utf-8", url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //使用Handler发送消息
                handler.sendMessage(m);
            }
        }) .start();
    }


    //关于动画的使用参考了网络代码
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
                    //手指抬起的时候要把currentDirection设置为0，这样下次不管向哪拉，都与当前的不同
                    // （其实在ACTION_DOWN里写了之后这里就用不着了……）
                    currentDirection = 0;
                    lastDirection = 0;
                    break;
            }
            return false;
        }
    };

    //实现按两次退出程序
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

