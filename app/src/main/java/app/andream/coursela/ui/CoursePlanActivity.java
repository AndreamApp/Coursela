package app.andream.coursela.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import app.andream.coursela.R;
import app.andream.coursela.bean.CoursePlans;
import app.andream.coursela.utils.API;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CoursePlanActivity extends ResponseActivity<CoursePlans>
    implements Toolbar.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener
{

    Toolbar toolbar;
    SwipeRefreshLayout refresh;
    RecyclerView planRecyclerView;
    View fab;

    CoursePlanAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan);
        initViews();
        // init data
        refresh.setRefreshing(true);
        onRefresh();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setOnMenuItemClickListener(this);
        setSupportActionBar(toolbar);

        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener( v -> {
            onAddClicked();
        });

        planRecyclerView = findViewById(R.id.lv_plan);
        adapter = new CoursePlanAdapter(course -> {
            startActivity(new Intent(CoursePlanActivity.this, CourseDetailActivity.class));
        });
        planRecyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        planRecyclerView.setLayoutManager(llm);
        planRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                refresh.setEnabled(llm.findFirstCompletelyVisibleItemPosition() == 0);
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onRefresh() {
        new PlanTask().execute();
    }

    @Override
    public void onChanged(@Nullable CoursePlans plans) {
        super.onChanged(plans);
        refresh.setRefreshing(false);
        if(plans != null && plans.status) {
            adapter.setPlans(plans);
        }
    }

    public void onAddClicked() {}

    public void onSearch(String key) {
        Intent i = new Intent(this, CourseSearchActivity.class);
        startActivityForResult(i, 100);
    }

    public void onScheduleClicked() {
        startActivity(new Intent(CoursePlanActivity.this, TableActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        myActionMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // TODO: change to course plan
                return true;
            }
        });

        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint("Course keyword...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO
                onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });

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


    public class PlanTask extends AsyncTask<String, Void, CoursePlans> {
        @Override
        protected CoursePlans doInBackground(String... strings) {
            CoursePlans plans = null;
            try {
                plans = API.coursePlan();
            } catch (ANError anError) {
                anError.printStackTrace();
            }
            return plans;
        }

        @Override
        protected void onPostExecute(CoursePlans plans) {
            onChanged(plans);
        }
    }

    public interface OnPlanClicked {
        void onPlanClicked(CoursePlans.Course course);
    }

    public static class CoursePlanAdapter extends RecyclerView.Adapter<CoursePlanAdapter.ViewHolder> {
        CoursePlans plans;
        OnPlanClicked listener;

        public CoursePlanAdapter(OnPlanClicked listener) {
            this.listener = listener;
        }

        public CoursePlans getPlans() {
            return plans;
        }

        public void setPlans(CoursePlans plans) {
            this.plans = plans;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_plan, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            holder.bind(plans.data.get(i));
        }

        @Override
        public int getItemCount() {
            if (plans == null || plans.data == null) {
                return 0;
            }
            return plans.data.size();
        }


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView name, teacher, stuCnt;
            ImageView status;
            CoursePlans.Course course;

            ViewHolder(View v) {
                super(v);
                v.setOnClickListener(view -> {
                    if(listener != null) {
                        listener.onPlanClicked(course);
                    }
                });
                name = v.findViewById(R.id.tv_course_name);
                teacher = v.findViewById(R.id.tv_course_teacher);
                stuCnt = v.findViewById(R.id.tv_course_stu_cnt);
                status = v.findViewById(R.id.course_state);
            }

            void bind(CoursePlans.Course course) {
                this.course = course;
                name.setText(course.course_name);
                teacher.setText(course.teacher + " " + course.academy);
                stuCnt.setText(course.student_cnt + " people");
                status.setImageResource(
                        "success".equals(course.status) ? R.drawable.state_green
                        : "failed".equals(course.status) ? R.drawable.state_red
                        : R.drawable.state_yellow
                );
            }
        }
    }
}
