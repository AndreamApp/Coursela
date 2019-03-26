package app.andream.coursela.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import app.andream.coursela.R;
import app.andream.coursela.bean.TeacherDetail;
import app.andream.coursela.utils.API;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class TeacherDetailActivity extends ResponseActivity<TeacherDetail> {

    View progress;
    TextView teacherName, academy, title, email;
    TextView[] code = new TextView[3];
    TextView[] name = new TextView[3];

    String key_name, key_academy;
    TeacherDetail teacherDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);
        initViews();
        key_name = getIntent().getStringExtra("name");
        key_academy = getIntent().getStringExtra("academy");
        refresh();
    }

    private void initViews() {
        progress = findViewById(R.id.progress);
        teacherName = findViewById(R.id.tv_teacher_name);
        academy = findViewById(R.id.tv_academy);
        title = findViewById(R.id.tv_title);
        email = findViewById(R.id.tv_email);
        code[0] = findViewById(R.id.tv_taken_course_code1);
        name[0] = findViewById(R.id.tv_taken_course_name1);
        code[1] = findViewById(R.id.tv_taken_course_code2);
        name[1] = findViewById(R.id.tv_taken_course_name2);
        code[2] = findViewById(R.id.tv_taken_course_code3);
        name[2] = findViewById(R.id.tv_taken_course_name3);
    }

    public void setProgress(boolean state) {
        if(state) {
            progress.setVisibility(View.VISIBLE);
            findViewById(R.id.ll).setVisibility(View.INVISIBLE);
        }
        else {
            progress.setVisibility(View.GONE);
            findViewById(R.id.ll).setVisibility(View.VISIBLE);
        }
    }

    public void refresh() {
        setProgress(true);
        new DetailTask().execute(key_name, key_academy);
    }

    @Override
    public void onChanged(@Nullable TeacherDetail detail) {
        super.onChanged(detail);
        setProgress(false);
        if(detail == null || !detail.status) {
            showState(R.string.state_failed, v -> {
                refresh();
            });
        }
        else {
            this.teacherDetail = detail;
            TeacherDetail.Teacher t = detail.data;
            teacherName.setText(t.name);
            academy.setText(t.academy);
            title.setText(t.title);
            email.setText(t.email);
            if(t.courses != null) {
                for(int i = 0; i < t.courses.size(); i++) {
                    code[i].setText(t.courses.get(i).course_code);
                    name[i].setText(t.courses.get(i).course_name);
                    code[i].setVisibility(View.VISIBLE);
                    name[i].setVisibility(View.VISIBLE);
                }
                for(int i = t.courses.size(); i < 3; i++) {
                    code[i].setVisibility(View.GONE);
                    name[i].setVisibility(View.GONE);
                }
            }
        }
    }

    public class DetailTask extends AsyncTask<String, Void, TeacherDetail> {
        @Override
        protected TeacherDetail doInBackground(String... strings) {
            TeacherDetail td = null;
            try {
                td = API.teacherDetail(strings[0], strings[1]);
            } catch (ANError anError) {
                anError.printStackTrace();
            }
            return td;
        }

        @Override
        protected void onPostExecute(TeacherDetail td) {
            onChanged(td);
        }
    }

}
