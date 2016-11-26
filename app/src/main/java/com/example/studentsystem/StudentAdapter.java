package com.example.studentsystem;

import android.content.Context;
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
import java.util.List;

import static com.example.studentsystem.MainActivity.*;
import static com.example.studentsystem.SearchActivity.search;
import static com.example.studentsystem.SearchActivity.searchStudentAdapter;
import static com.example.studentsystem.SearchActivity.searchStudentList;

/**
 * Created by ljh on 2016/11/18.
 */

public class StudentAdapter extends ArrayAdapter<Student> {
    private int resourceId;
    private Context mContext;
    private float downX = 0;
    private float lastXOffset = 0;
    private boolean isRight = false;
    private int lastPosition = -1;
    private boolean closeOtherTag = false;




    public StudentAdapter(Context context, int textViewResourceId,
                          List<Student> studentList) {
        super(context, textViewResourceId, studentList);
        resourceId = textViewResourceId;
        mContext = context;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Student student = getItem(position);
        View view;
        ViewHolder viewHolder;

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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.contentLayout.getLayoutParams();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        viewHolder.contentLayout.setLayoutParams(params);
        viewHolder.studentNameTextView.setText(student.getName());
        viewHolder.studentIdTextView.setText(student.getId());
        viewHolder.horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final View view = v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        if ( (lastPosition != -1) && (lastPosition != position)) {
                            View openedItemView = getViewByPosition(listView, (lastPosition + 1));
                            if (openedItemView != null) {
                                final HorizontalScrollView horizontalScrollView = ((HorizontalScrollView)
                                        openedItemView.findViewById(R.id.horizontal_scrollview));
                                horizontalScrollView.smoothScrollTo(0, 0);
                                lastPosition = -1;
                                closeOtherTag = true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getX() > lastXOffset) {
                            isRight = true;
                        } else {
                            isRight = false;
                        }
                        lastXOffset = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        final float distance = Math.abs(event.getX() - downX);
                        if (distance == 0.0) {
                            if (lastPosition != -1 && lastPosition == position) {
                                if ((view.getWidth() - downX) > dpToPx(96)) {
                                    v.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((HorizontalScrollView) view).fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;
                                        }
                                    });

                                } else {
                                    v.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((HorizontalScrollView) view).fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;
                                        }
                                    });
                                    if (search) {
                                        db.delete("Student", "id = ?", new String[]{searchStudentList.get(position).getId()});
                                        searchStudentList.remove(position);
                                        searchStudentAdapter.notifyDataSetChanged();
                                    } else {
                                        db.delete("Student", "id = ?", new String[]{studentList.get(position).getId()});
                                        studentList.remove(position);
                                        studentAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else if (lastPosition == -1) {
                                if (closeOtherTag) {
                                    closeOtherTag = false;
                                } else {
                                    ItemActivity.actionStart(getContext(), "" + position);
                                }
                            }
                        }  else if (distance>0 && distance<dpToPx(50)) {
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRight) {
                                        ((HorizontalScrollView) view).fullScroll(View.FOCUS_RIGHT);
                                    } else {
                                        ((HorizontalScrollView)view)
                                                .fullScroll(View.FOCUS_LEFT);
                                    }
                                }
                            });
                        } else {
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRight) {
                                        if(lastPosition != -1) {
                                            ((HorizontalScrollView) view)
                                                    .fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;
                                        } else {
                                            drawerLayout.openDrawer(navigationView);
                                        }
                                    } else {
                                        ((HorizontalScrollView)view)
                                                .fullScroll(View.FOCUS_RIGHT);
                                        lastPosition = position;
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

    public int dpToPx(int dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * ((float) dp)+0.5);
    }
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


    class ViewHolder {
        private TextView studentNameTextView;
        private TextView studentIdTextView;
        private LinearLayout contentLayout;
        private HorizontalScrollView horizontalScrollView;
    }
}
