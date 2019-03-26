package app.andream.coursela.utils;

import android.content.Context;
import android.content.Intent;

import app.andream.coursela.ui.CourseDetailActivity;

/**
 * Created by Andream on 2019/3/26.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class ActivityLauncher {

    public static void searchCourse(Context context, String key) {
        Intent i = new Intent(context, CourseDetailActivity.class);
        i.putExtra("search_key", key);
        context.startActivity(i);
    }
}
