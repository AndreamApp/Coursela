package app.andream.coursela.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.error.ANError;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import app.andream.coursela.R;
import app.andream.coursela.bean.Table;
import app.andream.coursela.utils.API;

/**
 * Created by Andream on 2019/3/22.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class TableActivity extends ResponseActivity<CourseIndexWrapper>
    implements TableView.OnCourseClickListener{

    Toolbar mToolBar;
    ViewPager mViewPager;               // Table pager, every page show a week's courses
    WeekPagerAdapter mAdapter;          // Table pager adapter
    SwipeRefreshLayout mRefresh;        // refresher, only show it in programming, as it conflicts with pager

    int mThisWeekNum;                   // Record current week
    Calendar semesterStartDate;         // Record the startup date of semester, for calculating current week

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_table);

        mToolBar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolBar);
        mRefresh = findViewById(R.id.refresh);
        mViewPager = findViewById(R.id.table_view_pager);

        // initially show table page
        setVisiblePage(0);

        // setup adapter
        mAdapter = new WeekPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        // setup model, and refresh
//        mRefresh.setColorSchemeColors(getPrimiryColor(), getPrimiryColor(), getAccentColor());
        mRefresh.setEnabled(false);

        // setup tool bar and menu
//        mToolBar.setOnMenuItemClickListener(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                TextView tv = findViewById(R.id.table_week_btn);
//                tv.setText("第"+(position+1)+"周");
                mToolBar.setTitle("Week " + (position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh(false);
    }

    public void setVisiblePage(int page) {
        if(0 == page) {
            mViewPager.setVisibility(View.VISIBLE);
            findViewById(R.id.fragment).setVisibility(View.GONE);
        }
        else if(1 == page) {
            mViewPager.setVisibility(View.GONE);
            findViewById(R.id.fragment).setVisibility(View.VISIBLE);
        }
    }

    public void refresh(boolean fromNetwork) {
        if(mViewPager.getVisibility() == View.VISIBLE) {
            if (mAdapter.getCount() == 0) {
                showState(R.string.state_loading);
            }
            mRefresh.setRefreshing(true);
            new TableTask().execute(fromNetwork);
        }
    }

    @Override
    public void onChanged(@Nullable CourseIndexWrapper wrapper) {
        super.onChanged(wrapper);

        mRefresh.setRefreshing(false);
        if (wrapper != null && wrapper.status) {
            Calendar startDate = wrapper.getSemesterStartDate();
            if(startDate != null) {
                setSemesterStartDate(startDate);
            }

            boolean isEmpty = mAdapter.getCount() == 0;
            mAdapter.wrapper = wrapper;
            mAdapter.notifyDataSetChanged();
            if (isEmpty) {
                switchToCurrWeek();
            }

            // show empty
            if (mAdapter.getCount() == 0) {
                showState(R.string.state_empty);
            }
            // show content
            else {
                hideState();
            }
        } else {
            // show error
            if (mAdapter.getCount() == 0) {
                showState(R.string.state_failed, v -> refresh(true));
            }
            // keep old data
            else {
                hideState();
            }
        }
    }

    private void switchToCurrWeek() {
        mViewPager.setCurrentItem(mThisWeekNum);
    }

    @Override
    protected boolean showState(boolean show, int resId, View.OnClickListener clickHandler) {
        // content visibility as opposed to state's
        mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        return super.showState(show, resId, clickHandler);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getAdapter() != null && mViewPager.getAdapter().getCount() > 0
                && mThisWeekNum != mViewPager.getCurrentItem()) {
            mViewPager.setCurrentItem(mThisWeekNum);
        }
        else {
            finish();
        }
    }


    public Calendar getSemesterStartDate() {
        return semesterStartDate;
    }

    /**
     * 根据学期开始日期，计算当前周。当前周需要及时更新，避免用户被过时的当前周误导。
     * 目前每次启动Activity都会计算一次
     * TODO: 从服务器获取当前学期开始日期
     *
     * @param semesterStartDate 学期开始日期
     */
    public void setSemesterStartDate(Calendar semesterStartDate) {
        this.semesterStartDate = semesterStartDate;
        long days = (Calendar.getInstance().getTimeInMillis() - semesterStartDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);
        long weekNum = days / 7;
        mThisWeekNum = (int) weekNum;
    }

    static final long COURSE_CLICK_THRESH = 100;
    long course_last_click;
    @Override
    public void onCourseClicked(CourseIndex index) {
        long now = SystemClock.uptimeMillis();
        if (now - course_last_click > COURSE_CLICK_THRESH) {
            course_last_click = now;
        }
    }

    class WeekPagerAdapter extends FragmentPagerAdapter {

        CourseIndexWrapper wrapper;

        WeekPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TableWeekFragment.newInstance(wrapper.indexes, position + 1);
        }

        @Override
        public int getCount() {
            // wrapper.indexes.get(0) has no value
            return wrapper == null || wrapper.indexes == null ? 0 : wrapper.indexes.size() - 1;
        }
    }

    /**
     * The fragment of a single week page
     */
    public static class TableWeekFragment extends Fragment {
        List<Set<CourseIndex>> indexes;
        int week;

        TableView tableView;

        static TableWeekFragment newInstance(List<Set<CourseIndex>> indexes, int week) {
            TableWeekFragment f = new TableWeekFragment();

            f.indexes = indexes;
            f.week = week;
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            tableView = new TableView(inflater.getContext(), null);
            tableView.setIndexes(indexes);
            tableView.setCurrentWeek(week);
            // show course detail
            tableView.setOnCourseClickListener((TableActivity) getActivity());
            return tableView;
        }
    }

    public class TableTask extends AsyncTask<Boolean, Void, CourseIndexWrapper> {
        @Override
        protected CourseIndexWrapper doInBackground(Boolean... fromNetwork) {
            Table table = null;
            try {
                table = API.getTable(fromNetwork[0]);
            } catch (ANError anError) {
                anError.printStackTrace();
            }
            if(table != null) {
                return new CourseIndexWrapper(table);
            }
            return null;
        }

        @Override
        protected void onPostExecute(CourseIndexWrapper ciw) {
            onChanged(ciw);
        }
    }

}
