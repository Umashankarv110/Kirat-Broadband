package com.umashankar.kiratbroadbanduser.SharedPreferencesClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.umashankar.kiratbroadbanduser.LoginActivity;
import com.umashankar.kiratbroadbanduser.ModelClass.Customers;

public class SharedPrefUserLogin {

    //the constants
    private static final String SHARED_PREF_NAME = "userloginpref";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_MOBILE = "userMobile";
    private static final String KEY_LANDLINE = "userLandline";
    private static final String KEY_ID = "userLoginId";

    private static SharedPrefUserLogin mInstance;
    private static Context mCtx;

    private SharedPrefUserLogin(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefUserLogin getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefUserLogin(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(Customers users) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, users.getCustomerId());
        editor.putString(KEY_USERNAME, users.getName());
        editor.putString(KEY_MOBILE, users.getMobile());
        editor.putString(KEY_LANDLINE, users.getLandline());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILE, null) != null;
    }

    //this method will give the logged in user
    public Customers getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Customers(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_MOBILE, null),
                sharedPreferences.getString(KEY_LANDLINE, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
