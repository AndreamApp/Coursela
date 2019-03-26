package app.andream.coursela.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import app.andream.coursela.R;
import app.andream.coursela.bean.CourseDetail;
import app.andream.coursela.bean.User;
import app.andream.coursela.utils.API;
import app.andream.coursela.utils.ActivityLauncher;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CourseDetailActivity extends ResponseActivity<CourseDetail> {

    View progress;
    TextView courseName, courseCode, academy, teacher, classroom, stucnt, schedule, credit, hours;

    String course_code, class_no;
    CourseDetail courseDetail;

    private static final int STATE_PROGRESS = 0;
    private static final int STATE_SUCCESS = 1;
    private static final int STATE_FAILED = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initViews();
        course_code = getIntent().getStringExtra("course_code");
        class_no = getIntent().getStringExtra("class_no");
        refresh();
    }

    private void initViews() {
        progress = findViewById(R.id.progress);
        courseName = findViewById(R.id.tv_course_name);
        courseCode = findViewById(R.id.tv_course_code);
        academy = findViewById(R.id.tv_academy);
        teacher = findViewById(R.id.tv_teacher);
        classroom = findViewById(R.id.tv_classroom);
        stucnt = findViewById(R.id.tv_course_stu_cnt);
        schedule = findViewById(R.id.tv_schedule);
        credit = findViewById(R.id.tv_credit);
        hours = findViewById(R.id.tv_hours);
        teacher.setOnClickListener(v -> {
            if(courseDetail != null && courseDetail.data != null) {
                onTeacherClicked(courseDetail.data.teacher, courseDetail.data.academy);
            }
        });
        courseCode.setOnClickListener(v -> {
            String key = courseCode.getText().toString();
            if(!TextUtils.isEmpty(key)) {
                Intent i = new Intent(CourseDetailActivity.this, CoursePlanActivity.class);
                i.putExtra("search_key", key);
                startActivity(i);
                finish();
            }
        });
        academy.setOnClickListener(v -> {
            String key = academy.getText().toString();
            if(!TextUtils.isEmpty(key)) {
                Intent i = new Intent(CourseDetailActivity.this, CoursePlanActivity.class);
                i.putExtra("search_key", key);
                startActivity(i);
                finish();
            }
        });
    }

    public void setState(int state) {
        if(STATE_PROGRESS == state) {
            progress.setVisibility(View.VISIBLE);
            findViewById(R.id.ll).setVisibility(View.INVISIBLE);
            hideState();
        }
        else if(STATE_SUCCESS == state){
            progress.setVisibility(View.GONE);
            findViewById(R.id.ll).setVisibility(View.VISIBLE);
            hideState();
        }
        else {
            progress.setVisibility(View.GONE);
            findViewById(R.id.ll).setVisibility(View.INVISIBLE);
        }
    }

    public void refresh() {
        setState(STATE_PROGRESS);
        new DetailTask().execute(course_code, class_no);
    }

    private String translateWeekday(String s) {
        String[] src = {"一", "二", "三", "四", "五", "六", "日"};
        String[] dst = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for(int i = 0; i < src.length; i++) {
            s = s.replaceAll(src[i], dst[i]);
        }
        s = s.replaceAll("节", " Section");
        return s;
    }

    @Override
    public void onChanged(@Nullable CourseDetail detail) {
        super.onChanged(detail);
        if(detail == null || !detail.status) {
            setState(STATE_FAILED);
            showState(R.string.state_failed, v -> {
                refresh();
            });
            Snackbar.make(progress, "Network failed!", Snackbar.LENGTH_SHORT).show();
        }
        else {
            setState(STATE_SUCCESS);
            this.courseDetail = detail;
            CourseDetail.Course cd = detail.data;
            courseName.setText(cd.course_name);
            courseCode.setText(cd.course_code);
            academy.setText(cd.academy);
            teacher.setText(cd.teacher);
            String room = null;
            if(cd.schedule != null && cd.schedule.size() > 0) {
                room = cd.schedule.get(0).classroom;
            }
            if(TextUtils.isEmpty(room)) {
                room = "D1233";
            }
            classroom.setText(room);
            stucnt.setText(cd.student_cnt);
            StringBuilder sche = new StringBuilder();
            if(cd.schedule != null) {
                for(int i = 0; i < cd.schedule.size(); i++) {
                    CourseDetail.Course.Schedule s = cd.schedule.get(i);
                    sche.append("Week " + s.weeks + ", " + translateWeekday(s.classtime));
                    if(i != cd.schedule.size() - 1) {
                        sche.append("\n");
                    }
                }
            }
            schedule.setText(sche.toString());
            credit.setText(cd.credit);
            hours.setText(cd.hours_all);
        }
    }

    public void onTeacherClicked(String name, String academy) {
        Intent i = new Intent(CourseDetailActivity.this, TeacherDetailActivity.class);
        i.putExtra("name", name);
        i.putExtra("academy", academy);
        startActivity(i);
    }

    public class DetailTask extends AsyncTask<String, Void, CourseDetail> {
        @Override
        protected CourseDetail doInBackground(String... strings) {
            CourseDetail cd = null;
            try {
                cd = API.courseDetail(strings[0], strings[1]);
            } catch (ANError anError) {
                anError.printStackTrace();
            }
            return cd;
        }

        @Override
        protected void onPostExecute(CourseDetail cd) {
            onChanged(cd);
        }
    }
}
