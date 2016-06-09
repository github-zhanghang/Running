package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.running.beans.NearUserInfo;
import com.running.beans.UserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SearchConditionsActivity extends AppCompatActivity {
    public static final String SearchConditions = MyApplication.HOST + "SearchConditionServlet";
    private Spinner genderSpinner, nlSpinner, shengSpinner, shiSpinner;
    private List<UserInfo> mUserInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_conditions);
        initViews();
        initData();
    }


    private void initViews() {
        mUserInfoList = new ArrayList<>();
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        nlSpinner = (Spinner) findViewById(R.id.nianling_spinner);
    }

    private void initData() {

    }

    public void searchUser(View view) {
        String gender = "男";
        int age = 20;
        String address = "河南省安阳市";

        request(gender, age, address);
    }

    private void request(String gender, int age, String address) {
        OkHttpUtils.get()
                .url(SearchConditions)
                .addParams("sex", gender)
                .addParams("age", age + "")
                .addParams("address", address)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("SearchConditions", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("test123", "SearchConditionsActivity: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                UserInfo userInfo =
                                        new Gson().fromJson(object.toString(), NearUserInfo.class);
                                mUserInfoList.add(userInfo);
                            }
                            Intent intent =
                                    new Intent(SearchConditionsActivity.this, ConFriendActivity.class);
                            intent.putExtra("SearchConditions", (Serializable) mUserInfoList);
                            startActivity(intent);
                            // mNearByAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
