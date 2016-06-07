package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.running.adapters.NearByAdapter;
import com.running.beans.NearUserInfo;
import com.running.beans.UserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class NearbyActivity extends AppCompatActivity {

    public static final String NearbyServlet
            = "http://10.201.1.185:8080/Running/NearbyServlet";
    private ListView  mListView;
    private List<NearUserInfo> mUserInfoList;
    private NearByAdapter mNearByAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        initViews();
        initData();
        Listener();
    }



    private void initViews() {
        mUserInfoList = new ArrayList<>();
        mNearByAdapter = new NearByAdapter(NearbyActivity.this,mUserInfoList);
        mListView = (ListView) findViewById(R.id.nearby_lv);
        mListView.setAdapter(mNearByAdapter);

    }
    private void initData() {
        request();
    }
    private void request() {
        double mLatitude = 0;
        double mLongitude = 0;
        OkHttpUtils.get()
                .url(NearbyServlet)
                .addParams("Uid",1+"")
                .addParams("Longitude",mLongitude+"")
                .addParams("Latitude",mLatitude+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("NearbyActivity", "onError: "+e.getMessage() );
                    }
                    @Override
                    public void onResponse(String response) {
                        Log.e("test123", "NearbyActivity: "+response );
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject object =  jsonArray.getJSONObject(i);
                                NearUserInfo userInfo =
                                        new Gson().fromJson(object.toString(),NearUserInfo.class);
                                mUserInfoList.add(userInfo);
                            }
                            mNearByAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void Listener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NearbyActivity.this,NewFriendInfoActivity.class);
                intent.putExtra("NearbyActivity",mUserInfoList.get(position));
                startActivity(intent);

            }
        });
    }

}
