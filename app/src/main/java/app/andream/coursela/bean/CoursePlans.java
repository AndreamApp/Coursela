package app.andream.coursela.bean;

import java.util.List;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class CoursePlans extends Response {

    public List<Course> data;

    public static class Course {
        public String status; // course is pending / success / failed

        public String course_name;
        public String course_code;
        public String credit;
        public String hours_all;
        public String teacher;

        public String class_no;
        public String academy;
        public String class_detail;
        public boolean is_exp;
        public String student_cnt;

        public List<Schedule> schedule;

        public static class Schedule{
            public String weeks;
            public String classtime;
            public String classroom;
        }
    }
}
