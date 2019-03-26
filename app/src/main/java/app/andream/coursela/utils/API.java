package app.andream.coursela.utils;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import app.andream.coursela.App;
import app.andream.coursela.bean.CourseDetail;
import app.andream.coursela.bean.CoursePlans;
import app.andream.coursela.bean.Courses;
import app.andream.coursela.bean.Table;
import app.andream.coursela.bean.TeacherDetail;
import app.andream.coursela.bean.User;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Andream on 2019/3/22.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class API {

    private static final String HOST = "https://cqu.andream.app";
    private static final String URL_LOGIN = "/api/v1/login";
    private static final String URL_LOGOUT = "/api/v1/logout";
    private static final String URL_GET_TABLE = "/api/v1/getTable";
    private static final String URL_SEARCH_COURSE = "/api/v1/searchCourse";
    // TODO: Add api
    private static final String URL_COURSE_PLAN = "/api/v1/coursePlan";
    private static final String URL_COURSE_DETAIL = "/api/v1/courseDetail";
    private static final String URL_TEACHER_DETAIL = "/api/v1/teacherDetail";
//    private static final String URL_GET_GRADE = "/api/v1/getGrade";
//    private static final String URL_GET_EXAMS = "/api/v1/getExams";
//    private static final String URL_LIKE = "/api/v1/like";
//    private static final String URL_CRASH = "/api/v1/crash";
//    private static final String URL_UPLOAD_FEEDBACK = "/api/v1/uploadFeedback";
//    private static final String URL_GET_FEEDBACKS = "/api/v1/getFeedbacks";
//    private static final String URL_CHECK_UPDATE = "/api/v1/checkUpdate";


    private static OkHttpClient.Builder trustAll() {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            // Install the all-trusting trust manager
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager

        } catch (Exception e) {
            e.printStackTrace();
        }

        final SSLSocketFactory sslSocketFactory = sslContext == null ? null : sslContext.getSocketFactory();

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
    }

    private static OkHttpClient withCookie(Context context) {
        CookieJar cookieJar =  new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        return trustAll()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build();
    }

    private static OkHttpClient withSaveOnlyCookie(Context context) {
        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context)){
            @Override
            public synchronized List<Cookie> loadForRequest(HttpUrl url) {
                return new ArrayList<>();
            }
        };
        return trustAll()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build();
    }

    public static boolean cookieExpired(Context context) {
        SharedPrefsCookiePersistor persistor = new SharedPrefsCookiePersistor(context);
        List<Cookie> cookies = persistor.loadAll();
        boolean res = false;
        // no cookie, treat as expired
        if (cookies == null || cookies.size() == 0) {
            res = true;
        } else {
            for (Cookie c : cookies) {
                if (c.expiresAt() < System.currentTimeMillis()) {
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    public static void clearCookies(Context context){
        SharedPrefsCookiePersistor persistor = new SharedPrefsCookiePersistor(context);
        persistor.clear();
    }

    public static boolean logout(Context context) throws ANError {
        // clear cookie
        clearCookies(context);

        // clear cache
        App.context().getSharedPreferences("cache", Context.MODE_PRIVATE)
                .edit()
                .putString("user_profile", "")
                .apply();

        ANRequest request = AndroidNetworking.post(HOST + URL_LOGOUT)
                .setPriority(Priority.LOW)
                .setOkHttpClient(withCookie(App.context()))
                .getResponseOnlyFromNetwork()
                .build();

        ANResponse response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject object = (JSONObject) response.getResult();
            try {
                return object.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw response.getError();
        }
    }

    public static User login(String stunum, String password) throws ANError {
        ANRequest request = AndroidNetworking.post(HOST + URL_LOGIN)
                .addBodyParameter("stunum", stunum)
                .addBodyParameter("password", password)
                .setPriority(Priority.LOW)
                .setOkHttpClient(withSaveOnlyCookie(App.context()))
                .getResponseOnlyFromNetwork()
                .build();
        ANResponse response = request.executeForObject(User.class);

        if (response.isSuccess()) {
            return (User) response.getResult();
        } else {
            throw response.getError();
        }
    }

    @Deprecated
    public static User login(Context context, String stunum, String password) throws IOException {
        OkHttpClient client = withSaveOnlyCookie(context);
        Request request = new Request.Builder()
                .url(HOST + URL_LOGIN + "?stunum=" + stunum + "&password=" + password)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == 200) {
            return new Gson().fromJson(response.body().string(), User.class);
        } else {
            throw new IOException("Response with code " + response.code());
        }
    }


    public static Table getTable(boolean fromNetwork) throws ANError {
        ANRequest.GetRequestBuilder builder = AndroidNetworking.get(HOST + URL_GET_TABLE)
                .setPriority(Priority.LOW)
                .setOkHttpClient(withCookie(App.context()));
        if (!fromNetwork) {
            builder.getResponseOnlyIfCached();
        } else {
            builder.getResponseOnlyFromNetwork();
        }

        ANRequest request = builder.build();
        ANResponse response = request.executeForObject(Table.class);

        if (response.isSuccess()) {
            return (Table) response.getResult();
        } else {
            throw response.getError();
        }
    }


    // TODO
    public static CoursePlans coursePlan() throws ANError {
        ANRequest.GetRequestBuilder builder = AndroidNetworking.get(HOST + URL_SEARCH_COURSE)
                .addQueryParameter("key", "软件")
                .setPriority(Priority.LOW)
                .setOkHttpClient(withCookie(App.context()));

        ANRequest request = builder.build();
        ANResponse response = request.executeForObject(CoursePlans.class);

        if (response.isSuccess()) {
            return (CoursePlans) response.getResult();
        } else {
            throw response.getError();
        }
    }


    // TODO
    public static CourseDetail courseDetail(String code, String no) throws ANError {
        ANRequest.GetRequestBuilder builder = AndroidNetworking.get(HOST + URL_COURSE_DETAIL)
                .addQueryParameter("code", code)
                .addQueryParameter("no", no)
                .setPriority(Priority.LOW)
                .setOkHttpClient(withCookie(App.context()));

        ANRequest request = builder.build();
        ANResponse response = request.executeForObject(CourseDetail.class);

        if (response.isSuccess()) {
            return (CourseDetail) response.getResult();
        } else {
            throw response.getError();
        }
    }

    // TODO
    public static TeacherDetail teacherDetail(String key) throws ANError {
        ANRequest.GetRequestBuilder builder = AndroidNetworking.get(HOST + URL_TEACHER_DETAIL)
                .addQueryParameter("key", key)
                .setPriority(Priority.LOW)
                .setOkHttpClient(withCookie(App.context()));

        ANRequest request = builder.build();
        ANResponse response = request.executeForObject(TeacherDetail.class);

        if (response.isSuccess()) {
            return (TeacherDetail) response.getResult();
        } else {
            throw response.getError();
        }
    }
}
