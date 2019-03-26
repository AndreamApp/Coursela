package app.andream.coursela.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.List;

import app.andream.coursela.R;
import app.andream.coursela.bean.Courses;
import app.andream.coursela.utils.API;

/**
 * Created by Andream on 2019/3/26.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CourseSearchFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout refresh;
    RecyclerView searchRecyclerView;
    View fab;

    String key;
    boolean needRefresh = true;
    boolean started = false;
    CourseSearchAdapter adapter;

    List<Courses.Course> checkedCourses;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_course_search, container, false);
        initViews(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        started = true;
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        started = false;
    }

    void initViews(View v) {
        refresh = v.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);

        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            onAddClicked();
        });

        searchRecyclerView = v.findViewById(R.id.lv_search);
        adapter = new CourseSearchAdapter(new OnCourseItemListener() {
            @Override
            public void onCourseClicked(Courses.Course course) {
                Intent i = new Intent(getActivity(), CourseDetailActivity.class);
                i.putExtra("course_code", course.course_code);
                i.putExtra("class_no", course.class_no);
                startActivity(i);
            }

            @Override
            public void onCourseCheckChanged(Courses.Course course, boolean state) {
                if(state) {
                    checkedCourses.add(course);
                }
                else {
                    checkedCourses.remove(course);
                }
            }
        });
        searchRecyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        searchRecyclerView.setLayoutManager(llm);
        searchRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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


    public void refresh() {
        if(needRefresh) {
            refresh.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        new SearchTask().execute(key);
    }

    public void onChanged(@Nullable Courses results) {
//        super.onChanged(plans);
        refresh.setRefreshing(false);
        if(results != null && results.status) {
            checkedCourses = new ArrayList<>();
            adapter.setResults(results);
            needRefresh = false;
        }
    }

    public void onAddClicked() {
        CoursePlanActivity activity = (CoursePlanActivity) getActivity();
        if(activity != null) {
            activity.onCourseAdded(checkedCourses);
        }
    }

    public List<Courses.Course> getCheckedCourses() {
        return checkedCourses;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if(!key.equals(this.key)) {
            this.key = key;
            this.needRefresh = true;
        }
    }

    public class SearchTask extends AsyncTask<String, Void, Courses> {
        @Override
        protected Courses doInBackground(String... strings) {
            Courses results = null;
            try {
                results = API.searchCourse(strings[0]);
            } catch (ANError anError) {
                anError.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(Courses results) {
            onChanged(results);
        }
    }

    public interface OnCourseItemListener {
        void onCourseClicked(Courses.Course course);
        void onCourseCheckChanged(Courses.Course course, boolean state);
    }

    public static class CourseSearchAdapter extends RecyclerView.Adapter<CourseSearchAdapter.ViewHolder> {
        Courses results;
        OnCourseItemListener listener;

        public CourseSearchAdapter(OnCourseItemListener listener) {
            this.listener = listener;
        }

        public Courses getResults() {
            return results;
        }

        public void setResults(Courses results) {
            this.results = results;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_search, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            holder.bind(results.data.get(i));
        }

        @Override
        public int getItemCount() {
            if (results == null || results.data == null) {
                return 0;
            }
            return results.data.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView name, teacher, stuCnt;
            CheckBox status;
            Courses.Course course;

            ViewHolder(View v) {
                super(v);
                v.setOnClickListener(view -> {
                    if(listener != null) {
                        listener.onCourseClicked(course);
                    }
                });
                name = v.findViewById(R.id.tv_course_name);
                teacher = v.findViewById(R.id.tv_course_teacher);
                stuCnt = v.findViewById(R.id.tv_course_stu_cnt);
                status = v.findViewById(R.id.course_state);
            }

            void bind(Courses.Course course) {
                this.course = course;
                name.setText(course.course_name);
                teacher.setText(course.teacher + " " + course.academy);
                stuCnt.setText(course.student_cnt + " people");
                status.setChecked(false);
                status.setOnCheckedChangeListener((compoundButton, b) -> {
                    if(listener != null) {
                        listener.onCourseCheckChanged(course, b);
                    }
                });
            }
        }
    }
}
