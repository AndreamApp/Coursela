package app.andream.coursela.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import app.andream.coursela.bean.Response;
import app.andream.coursela.bean.User;

/**
 * Created by Andream on 2019/3/22.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class ResponseActivity<T extends Response> extends AppCompatActivity {

    public void onChanged(@Nullable T t) {
        if (t == null) {
            return;
        }
        if (t.status) {
            String msg = t.msg;
            if (msg != null) {
                Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            String err = t.err;
            if (err != null) {
                Snackbar.make(getWindow().getDecorView(), err, Snackbar.LENGTH_SHORT).show();
                if (err.equals("登录身份已过期")) {
//                    new LogoutTask(this).execute();
                }
            }
        }
    }
}
