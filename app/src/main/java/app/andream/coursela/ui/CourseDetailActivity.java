package app.andream.coursela.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.andream.coursela.R;
import app.andream.coursela.bean.CourseDetail;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CourseDetailActivity extends ResponseActivity<CourseDetail> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
    }
}
