package app.andream.coursela;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

/**
 * Created by Andream on 2019/3/22.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */

public class App extends Application {

    private static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // init network
        AndroidNetworking.initialize(this);
    }

    public static Application context() {
        return context;
    }
}