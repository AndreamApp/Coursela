package app.andream.coursela.bean;

/**
 * Created by Andream on 2019/3/22.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class User extends Response{

    public UserData data;

    public class UserData {
        public String stunum;
        public String name;
        public String sex;
        public String birthday;
        public String nation;
        public String academy;
        public String class_name;
        public String tel;
    }
}
