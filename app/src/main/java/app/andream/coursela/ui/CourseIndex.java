package app.andream.coursela.ui;

import android.support.annotation.NonNull;

import app.andream.coursela.bean.Table;

/**
 * Created by Andream on 2018/3/24.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */

public class CourseIndex implements Comparable<CourseIndex> {
    int weekday;
    int sectionStart;
    int sectionEnd;
    String classroom;
    Table.Course course;

    @Override
    public int compareTo(@NonNull CourseIndex o) {
        if (this.weekday == o.weekday) {
            if (this.sectionStart == o.sectionStart) {
                return this.sectionEnd - o.sectionEnd;
            }
            return this.sectionStart - o.sectionStart;
        }
        return this.weekday - o.weekday;
    }
}
