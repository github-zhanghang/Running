package com.running.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.running.android_main.MyApplication;
import com.running.android_main.R;
import com.running.android_main.RunMapActivity;
import com.running.myviews.MyStartButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class PaobuLeftFragment extends Fragment {
    private String mPath = MyApplication.HOST + "totalRecordServlet";
    private MyApplication mApplication;
    private View mLeftView;
    private MyStartButton mStartButton;
    private TextView mTotalDistanceText, mTotalTimeText, mTotalCount;
    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(1);

    //毫秒转成 时:分:秒 格式
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    DecimalFormat mDecimalFormat = new DecimalFormat("######0.00");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mApplication = (MyApplication) getActivity().getApplication();

        mLeftView = inflater.inflate(R.layout.paobu_left, null);
        initView();
        initListeners();
        return mLeftView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取跑步数据
        getRunData();
    }

    private void initView() {
        mStartButton = (MyStartButton) mLeftView.findViewById(R.id.startRun);
        mTotalDistanceText = (TextView) mLeftView.findViewById(R.id.run_totaldistance);
        mTotalTimeText = (TextView) mLeftView.findViewById(R.id.run_totaltime);
        mTotalCount = (TextView) mLeftView.findViewById(R.id.run_totalcount);
    }

    private void getRunData() {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "totaldata");
        request.add("uid", mApplication.getUserInfo().getUid());
        mRequestQueue.add(1, request, onResponseListener);
        mRequestQueue.start();
    }

    private OnResponseListener onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == 1) {
                String result = response.get();
                if (result != null && !result.equals("")) {
                    //Log.e("my", "paobuleft.result=" + result);
                    String[] data = result.split(",");
                    String dis = mDecimalFormat.format(Double.parseDouble(data[0]));
                    mTotalDistanceText.setText(dis);
                    mApplication.setDistance(dis);
                    mTotalTimeText.setText(mSimpleDateFormat.format(Integer.parseInt(data[1]) - 8 * 60 * 60 * 1000));
                    mTotalCount.setText(data[2]);
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            if (what == 1) {
                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void initListeners() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RunMapActivity.class));
            }
        });
    }
}
