package com.matthewwen.todoplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.matthewwen.todoplanner.object.section;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiRequest {
    private static final String TAG = "APIREQUEST";

    private static final String URL = "https://www.matthewwen.com/todo";

    @SuppressLint("DefaultLocale")
    public static String getSecurityDate() {
        Calendar r = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"), Locale.US);
        return String.format("%02d%02d%04dT%02d%02d%02d", r.get(Calendar.MONTH) + 1, r.get(Calendar.DAY_OF_MONTH),
                r.get(Calendar.YEAR), r.get(Calendar.HOUR_OF_DAY), r.get(Calendar.MINUTE), r.get(Calendar.SECOND));
    }

    public static void setPassword(Context context, String password) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        ((SharedPreferences.Editor) editor).putString("password", password);
        editor.apply();
    }

    public static String getPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("password", "");
    }

    public static String getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        assert digest != null;
        digest.reset();
        byte[] data = digest.digest(password.getBytes());
        return String.format("%0" + (data.length*2) + "x", new BigInteger(1, data));
    }

    public static String create_section(Context context, String sectionName, long date){
        String returnStr  = null;
        String msgForHash = String.format("{\"time\":\"%s\",\"password\":\"%s\"}", getSecurityDate(), getPassword(context));
        String hash       = getHash(msgForHash);
        @SuppressLint("DefaultLocale")
        String mUrl = String.format("%s/section/create?name=%s", URL, sectionName);
        if (date > 0) {
            @SuppressLint("DefaultLocale")
            String dateAppend = String.format("&date=%d", date);
            mUrl = mUrl + dateAppend;
        }
        OkHttpClient client = new OkHttpClient();
        Log.v("MAIN", getSecurityDate());
        Request request = new Request.Builder()
                .url(mUrl)
                .header("expires", getSecurityDate())
                .addHeader("Authorization", hash)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("MWEN", returnStr != null ? returnStr: "It is NULL");
        return returnStr;
    }

    public static ArrayList<section> get_section(Context context) {
        String returnStr  = null;
        String msgForHash = String.format("{\"time\":\"%s\",\"password\":\"%s\"}", getSecurityDate(), getPassword(context));
        String hash       = getHash(msgForHash);
        @SuppressLint("DefaultLocale")
        String mUrl = String.format("%s/section/", URL);
        OkHttpClient client = new OkHttpClient();
        Log.v("MAIN", getSecurityDate());
        Request request = new Request.Builder()
                .url(mUrl)
                .header("expires", getSecurityDate())
                .addHeader("Authorization", hash)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            returnStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("MWEN", returnStr != null ? returnStr: "It is NULL");

        return new ArrayList<>();
    }
}
