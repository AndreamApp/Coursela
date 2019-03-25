package app.andream.coursela.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import app.andream.coursela.R;
import app.andream.coursela.bean.CoursePlans;

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
    // tableFragment
    // searchFragment

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan);
        initViews();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);

        planFragment = new CoursePlanFragment();

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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, planFragment)
                .commit();
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

}
