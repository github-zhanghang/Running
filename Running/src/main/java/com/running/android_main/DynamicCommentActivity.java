package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.running.adapters.DynamicCommentItemAdapter;
import com.running.adapters.DynamicImgGridViewAdapter;
import com.running.beans.CommentBean;
import com.running.beans.DynamicImgBean;
import com.running.beans.SecondCommentBean;
import com.running.myviews.MyGridView;
import com.running.myviews.TopBar;
import com.running.utils.MySpan;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class DynamicCommentActivity extends AppCompatActivity implements MySpan.OnClickListener {

    private TextView mCommentImgHeaderPraiseCount;
    private ImageView mHeaderImg;
    private TextView mHeaderName;
    private TextView mHeaderTime;
    private TextView mHeaderContent;
    private MyGridView mHeaderGridView;
    private ImageView mHeaderPraiseImg;
    private TextView mHeaderPraiseCount;
    private List<CommentBean> mList;
    private DynamicCommentItemAdapter mAdapter;
    private DynamicImgBean mDynamicImgBean;
    private String url = "http://10.201.1.176:8080/RunningAppTest/dynamicOperateServlet";
    private CommentCallBack mCommentCallBack;
    private HashMap<String, Object> mMap = new HashMap<>();

    @Bind(R.id.dynamic_comment_topBar)
    TopBar mDynamicCommentTopBar;
    @Bind(R.id.dynamic_comment_listView)
    ListView mDynamicCommentListView;
    @Bind(R.id.dynamic_comment_footEdit)
    EditText mDynamicCommentFootEdit;
    @Bind(R.id.dynamic_comment_button)
    Button mDynamicCommentButton;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    for (int i = 0; i < mCommentCallBack.mCommentBeanList.size(); i++) {
                        mList.add(mCommentCallBack.mCommentBeanList.get(i));
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_comment);
        ButterKnife.bind(this);
        initData();
        addListener();
    }

    private void addListener() {
        mDynamicCommentTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                Toast.makeText(DynamicCommentActivity.this, "返回", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                Toast.makeText(DynamicCommentActivity.this, "分享", Toast.LENGTH_SHORT).show();

            }
        });

        mDynamicCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //自己的id和名字
                int uId0 = 3;
                String uName0 = "1003";
                //回复内容
                String content = mDynamicCommentFootEdit.getText().toString();
                //回复时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = format.format(new Date());
                SecondCommentBean bean = new SecondCommentBean((int) mMap.get("sFCId"), uId0,
                        uName0, (int) mMap.get("uId1"), (String) mMap.get("uName1"), content, time);
                mList.get((int)mMap.get("position")).getList().add(bean);
                Gson gson = new Gson();
                String secondComment = gson.toJson(bean);
                Log.d("TAG",secondComment);
                OkHttpUtils.post()
                        .url(url)
                        .addParams("appRequest","AddSecondComment")
                        .addParams("secondComment",secondComment)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e) {
                            }

                            @Override
                            public void onResponse(String response) {

                            }
                        });
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initData() {
        Intent intent = getIntent();
        int myId = intent.getIntExtra("myId", -1);
        mDynamicImgBean = (DynamicImgBean) intent.getSerializableExtra("dynamicBean");
        mList = new ArrayList<>();
        //HashMap<String, Object> map = new HashMap<>();

        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "ShowComment")
                .addParams("dId", "" + 1)
                //.addParams("dId",""+mDynamicImgBean.getdId())
                .addParams("myId", "" + 2)
                .build()
                .execute(mCommentCallBack = new CommentCallBack());

        //头部View
        View view = LayoutInflater.from(this).inflate(R.layout.dynamic_comment_img_header,
                mDynamicCommentListView, false);
        HeadInitView(view);
        Glide.with(this)
                .load(mDynamicImgBean.getHeadPhoto())
                .placeholder(R.drawable.head_photo)
                .into(mHeaderImg);
        mHeaderName.setText(mDynamicImgBean.getName());
        mHeaderTime.setText(mDynamicImgBean.getTime());
        mHeaderContent.setText(mDynamicImgBean.getContent());
        List<String> imgList = mDynamicImgBean.getImgList();
        DynamicImgGridViewAdapter adapter = new DynamicImgGridViewAdapter(this, imgList,
                mHeaderGridView);
        mHeaderGridView.setAdapter(adapter);
        mDynamicCommentListView.addHeaderView(view);
        mAdapter = new DynamicCommentItemAdapter(this, mList);
        mDynamicCommentListView.setAdapter(mAdapter);
    }

    //点击文字事件
    @Override
    public void onClick(View view, MySpan span) {
        Toast.makeText(DynamicCommentActivity.this, span.getId() + "：" + span.getContent(),
                Toast.LENGTH_SHORT).show();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getfCId()==span.getBean().getsFCId()) {
                mMap.put("position",i);
                mMap.put("sFCId",span.getBean().getsFCId());
                mMap.put("uId1", span.getBean().getuId0());
                mMap.put("uName1", span.getBean().getuName0());
                mDynamicCommentFootEdit.requestFocus();
            }
        }
    }

    /**
     * 初始化头部布局控件
     *
     * @param view
     */
    private void HeadInitView(View view) {
        mHeaderImg = (ImageView) view.findViewById(R.id.comment_imgHeader_Img);
        mHeaderName = (TextView) view.findViewById(R.id.comment_imgHeader_name);
        mHeaderTime = (TextView) view.findViewById(R.id.comment_imgHeader_time);
        mHeaderContent = (TextView) view.findViewById(R.id.comment_imgHeader_content);
        mHeaderGridView = (MyGridView) view.findViewById(R.id.comment_imgHeader_gridView);
        mHeaderPraiseImg = (ImageView) view.findViewById(R.id.comment_imgHeader_praiseImg);
        mHeaderPraiseCount = (TextView) view.findViewById(
                R.id.comment_imgHeader_praiseCount);
    }

    /**
     * 用来被适配器回调，适配器监听回复操作，一旦回复执行，底部editText获取焦点
     * 同时得到adapter返回的数据然后监听发送按钮刷新数据
     *
     * @param status
     */
    public void getFocus(boolean status) {
        if (status) {
            mDynamicCommentFootEdit.requestFocus();
            mMap = mAdapter.getMap();
        } else {
            return;
        }
    }

    class CommentCallBack extends StringCallback {
        List<CommentBean> mCommentBeanList = new ArrayList<>();

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            mCommentBeanList = gson.fromJson(response, new TypeToken<List<CommentBean>>() {
            }
                    .getType());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }).start();

        }
    }
}
