package app.andream.coursela.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import app.andream.coursela.R;
import app.andream.coursela.bean.CourseDetail;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CourseDetailActivity extends ResponseActivity<CourseDetail> {

    TextView courseName, courseCode, academy, teacher, classroom, stucnt, schedule, credit, hours;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initViews();
    }

    private void initViews() {
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
            // TODO: transfer teacher info
            startActivity(new Intent(CourseDetailActivity.this, TeacherDetailActivity.class));
        });
    }
}
