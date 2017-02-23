package com.example.studentsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.studentsystem.MainActivity.*;
import static com.example.studentsystem.SearchActivity.search;
import static com.example.studentsystem.SearchActivity.searchStudentAdapter;
import static com.example.studentsystem.SearchActivity.searchStudentList;

/**
 * Created by ljh on 2016/11/18.
 */

//学生的列表适配器类，列表item显示学号和姓名，要实现类似qq的滑动删除item效果
public class StudentAdapter extends ArrayAdapter<Student> {
    private int resourceId; //存储listview中list的id
    private Context mContext;   //存储上下文
    private float downX = 0;    //手指接触屏幕时的水平坐标
    private float lastXOffset = 0;  //上次水平移动距离
    private boolean isRight = false;    //是否是向右滑动
    private int lastPosition = -1;  //上次打开的item下标
    private boolean closeOtherTag = false;  //判断点击item是否是为了关闭其他已经打开的item


    public StudentAdapter(Context context, int textViewResourceId,
                          List<Student> studentList) {
        super(context, textViewResourceId, studentList);    //加载item
        resourceId = textViewResourceId;    //记下item的id
        mContext = context;     //记下item的上下文
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Student student = getItem(position);    //得到student
        View view;
        ViewHolder viewHolder;  //为了利用缓存来加载listview

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.studentIdTextView = (TextView) view.findViewById(R.id.student_id_text_view);
            viewHolder.studentNameTextView = (TextView) view.findViewById(R.id.student_name_text_view);
            viewHolder.contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
            viewHolder.horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontal_scrollview);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        //将数据填入item
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.contentLayout.getLayoutParams();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        viewHolder.contentLayout.setLayoutParams(params);
        viewHolder.studentNameTextView.setText(student.getName());
        viewHolder.studentIdTextView.setText(student.getId());

        //为item设置监听事件以实现滑动删除效果
        viewHolder.horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final View view = v;
                switch (event.getAction()) {
                    //手指接触屏幕
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();   //水平坐标
                        if ( (lastPosition != -1) && (lastPosition != position)) {
                            //上次已经滑开了一个item且这次点击的不是已打开的item
                            View openedItemView = getViewByPosition(listView, (lastPosition + 1));  //得到已打开的item
                            if (openedItemView != null) {
                                //关闭已打开的item
                                final HorizontalScrollView horizontalScrollView = ((HorizontalScrollView)
                                        openedItemView.findViewById(R.id.horizontal_scrollview));
                                horizontalScrollView.smoothScrollTo(0, 0);
                                lastPosition = -1;  //标志没有打开的item
                                closeOtherTag = true;   //这次点击是为了关闭其他item并不进入此item的内部
                            }
                        }
                        break;
                    //手指滑动
                    case MotionEvent.ACTION_MOVE:
                        //判断手指所在水平坐标来确定是否是向右滑
                        if (event.getX() > lastXOffset) {
                            isRight = true;
                        } else {
                            isRight = false;
                        }
                        //记录下手指离开屏幕时的的水平坐标
                        lastXOffset = event.getX();
                        break;
                    //手指离开屏幕
                    case MotionEvent.ACTION_UP:
                        //计算出移动距离
                        final float distance = Math.abs(event.getX() - downX);
                        //未移动即点击了item
                        if (distance == 0.0) {
                            if (lastPosition != -1 && lastPosition == position) {
                                //此次点击是为了关闭已打开的item
                                if ((view.getWidth() - downX) > dpToPx(96)) {
                                    //并未点击到删除区域
                                    v.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((HorizontalScrollView) view).fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;  //标志没有打开的item
                                        }
                                    });

                                } else {
                                    //此次点击是为了删除item
                                    v.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //关闭已打开的item
                                            ((HorizontalScrollView) view).fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;
                                        }
                                    });

                                    serverDelete(position);    //服务器删除
                                }
                            } else if (lastPosition == -1) {
                                //点击item是为了关闭item
                                if (closeOtherTag) {
                                    closeOtherTag = false;
                                } else {
                                    //显示item详情
                                    ItemActivity.actionStart(getContext(), "" + position);
                                }
                            }
                        }  else if (distance>0 && distance<dpToPx(50)) {
                            //手指离开屏幕时判断为已滑动且滑动距离不足触发显示或关闭
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    //右滑试图关闭删除按钮则重新显示
                                    if (isRight) {
                                        ((HorizontalScrollView) view).fullScroll(View.FOCUS_RIGHT);
                                    } else {
                                        //左滑试图显示删除按钮则关闭
                                        ((HorizontalScrollView)view)
                                                .fullScroll(View.FOCUS_LEFT);
                                    }
                                }
                            });
                        } else {
                            //滑动且满足触发条件
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    //右滑关闭删除按钮
                                    if (isRight) {
                                        if(lastPosition != -1) {
                                            ((HorizontalScrollView) view)
                                                    .fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;  //没有打开的item
                                        }
                                    } else {
                                        //左滑显示删除按钮
                                        ((HorizontalScrollView)view)
                                                .fullScroll(View.FOCUS_RIGHT);
                                        lastPosition = position;    //记录已打开的item
                                    }
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        return view;
    }

    //将dp单位转换成px
    public int dpToPx(int dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * ((float) dp)+0.5);
    }

    //通过position找到view
    private View getViewByPosition(ListView listView, int position) {
        int firstItemPos = listView.getFirstVisiblePosition();
        int lastItemPos = firstItemPos + listView.getChildCount() - 1;
        if (position < firstItemPos || position > lastItemPos) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            int childIndex = position - firstItemPos;
            return listView.getChildAt(childIndex);
        }
    }
    //服务器删除学生的handler
    private Handler handler=new Handler(){
        public void handleMessage(Message m){
            String post_result=m.obj + "";  //得到线程传递的message
            //验证服务器返回的结果
            if ("ok".equals(post_result)) {
                //判断该item是否是在searchActivity中被删除
                if (search) {
                    //从数据库和学生列表中删除该学生并刷新SearchActivity中的listview
                    db.delete("Student", "id = ?", new String[]{searchStudentList.get(m.arg1).getId()});
                    searchStudentList.remove(m.arg1); //列表删除
                    searchStudentAdapter.notifyDataSetChanged();    //更新列表
                } else {

                    Log.d("test", "deleteposition: " + m.arg1);
                    //是在MainActivity中点击了删除并刷新MainActivity中的listview
                    db.delete("Student", "id = ?", new String[]{studentList.get(m.arg1).getId()});
                    studentList.remove(m.arg1);
                    studentAdapter.notifyDataSetChanged();
                }
                Toast.makeText(getContext(), "删除成功",
                        Toast.LENGTH_SHORT).show();
            } else if ("error".equals(post_result)){
                Toast.makeText(getContext(), "出错，未删除",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "网络异常",
                        Toast.LENGTH_SHORT).show();
            }


        }
    };
    //服务器删除学生
    private void serverDelete(final int position) {
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());  //本地文件
        //创建新的线程提交要删除的student_id
        new Thread(new Runnable() {
            @Override
            public void run() {
                String id = studentList.get(position).getId();
                //将id密码放入参数中已发送给服务器
                Map<String, String> params = new HashMap<String, String>();
                String account = preferences.getString("account", "");  //获得当前保存的用户名
                params.put("account", account);
                params.put("id", id);
                //使用Message封装非UI线程的消息
                Message m=new Message();
                //记录下学生在列表中的位置
                m.arg1 = position;
                try {
                    //使用message的参数来获得服务器返回的数据
                    URL url = new URL("http://www.hustljh.cn/android/delete");
                    m.obj = HttpUtils.submitPostData(params, "utf-8", url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //使用Handler发送消息
                handler.sendMessage(m);
            }
        }) .start();
    }

    //view缓存类
    class ViewHolder {
        private TextView studentNameTextView;
        private TextView studentIdTextView;
        private LinearLayout contentLayout;
        private HorizontalScrollView horizontalScrollView;
    }
}
