package app.andream.coursela.bean;

import java.util.List;

/**
 * Created by Andream on 2019/3/25.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class TeacherDetail extends Response {

    public class Teacher {

        public String name;
        public String academy;
        public String title;
        public String email;
        public List<Course> courses;

        public class Course {
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

            public class Schedule{
                public String weeks;
                public String classtime;
                public String classroom;
            }
        }
    }
}
