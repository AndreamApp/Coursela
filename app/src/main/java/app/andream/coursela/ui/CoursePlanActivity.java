package app.andream.coursela.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.List;

import app.andream.coursela.R;
import app.andream.coursela.bean.CoursePlans;
import app.andream.coursela.bean.Courses;
import app.andream.coursela.utils.API;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CoursePlanActivity extends ResponseActivity<CoursePlans>
    implements Toolbar.OnMenuItemClickListener
{

    Toolbar toolbar;
    CoursePlanFragment planFragment;
    CourseSearchFragment searchFragment;
    MenuItem searchItem;
    // tableFragment

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        planFragment = new CoursePlanFragment();
        searchFragment = new CourseSearchFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, planFragment)
                .commit();
    }

    @Override
    public void onChanged(@Nullable CoursePlans plans) {
        super.onChanged(plans);
    }

    public void onSearch(String key) {
        searchFragment.setKey(key);
        if(searchFragment.started) {
            searchFragment.refresh();
        }
    }

    public void onScheduleClicked() {
        startActivity(new Intent(CoursePlanActivity.this, TableActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchItem = myActionMenuItem;
        myActionMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, searchFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, planFragment)
                        .commit();
                return true;
            }
        });

        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint("Course keyword...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });


        String key = getIntent().getStringExtra("search_key");
        if(!TextUtils.isEmpty(key)) {
            expandSearch();
            searchView.setQuery(key, true);
//            onSearch(key);
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_schedule:
                onScheduleClicked();
                break;
        }
        return true;
    }

    public void expandSearch() {
        searchItem.expandActionView();
        searchItem.getActionView().requestFocus();
    }

    public void onCourseAdded(List<Courses.Course> checkedCourses) {
        if(checkedCourses.size() <= 0) {
            return;
        }
        // TODO: put Courses to cloud
        List<CoursePlans.Course> courses = new ArrayList<>();
        for(Courses.Course c : checkedCourses) {
            CoursePlans.Course alter = new CoursePlans.Course();
            alter.course_name = c.course_name;
            alter.course_code = c.course_code;
            alter.credit = c.credit;
            alter.hours_all = c.hours_all;
            alter.teacher = c.teacher;
            alter.class_no = c.class_no;
            alter.academy = c.academy;
            alter.class_detail = c.class_detail;
            alter.is_exp = c.is_exp;
            alter.student_cnt = c.student_cnt;
            alter.schedule = new ArrayList<>();
            for(Courses.Course.Schedule s : c.schedule) {
                CoursePlans.Course.Schedule ps = new CoursePlans.Course.Schedule();
                ps.classroom = s.classroom;
                ps.classtime = s.classtime;
                ps.weeks = s.weeks;
                alter.schedule.add(ps);
            }
            courses.add(alter);
        }

        CoursePlans originPlan = planFragment.adapter.getPlans();
        if(originPlan != null && originPlan.data != null && originPlan.data.size() > 0) {
            courses.addAll(originPlan.data);
        }

        searchItem.collapseActionView();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, planFragment)
                .commit();
        new PutPlanTask().execute(courses);
    }

    public class PutPlanTask extends AsyncTask<List<CoursePlans.Course>, Void, CoursePlans> {
        @Override
        protected CoursePlans doInBackground(List<CoursePlans.Course>... plans) {
            CoursePlans res = null;
            try {
                res = API.putCoursePlan(plans[0]);
            } catch (ANError anError) {
                anError.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(CoursePlans plans) {
            onChanged(plans);
        }
    }
}
