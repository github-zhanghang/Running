package com.running.android_main;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.running.adapters.DynamicCommentItemAdapter;
import com.running.beans.CommentBean;
import com.running.beans.DynamicImgBean;
import com.running.beans.SecondCommentBean;
import com.running.beans.UserInfo;
import com.running.myviews.TopBar;
import com.running.myviews.ninegridview.ImageInfo;
import com.running.myviews.ninegridview.NineGridView;
import com.running.myviews.ninegridview.preview.ClickNineGridViewAdapter;
import com.running.utils.GlideCircleTransform;
import com.running.utils.MySpan;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class DynamicCommentActivity extends AppCompatActivity implements MySpan.OnClickListener {

    private ImageView mHeaderImg;
    private TextView mHeaderName;
    private TextView mHeaderTime;
    private TextView mHeaderContent;
    private NineGridView mHeaderGridView;
    private ImageView mHeaderPraiseImg;
    private TextView mHeaderPraiseCount;
    private ImageView mHeaderCommentImg;
    private TextView mHeaderCommentCount;
    private List<CommentBean> mList;
    private DynamicCommentItemAdapter mAdapter;
    private DynamicImgBean mDynamicImgBean;
    private String url = MyApplication.HOST + "dynamicOperateServlet";
    private CommentCallBack mCommentCallBack;
    private HashMap<String, Object> mMap = new HashMap<>();
    private int status = 2;


    @Bind(R.id.dynamic_comment_topBar)
    TopBar mDynamicCommentTopBar;
    @Bind(R.id.dynamic_comment_listView)
    ListView mDynamicCommentListView;
    @Bind(R.id.dynamic_comment_bottom_layout)
    LinearLayout mCommentBottomLayout;
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
        mCommentBottomLayout.setVisibility(View.GONE);
        initData();
        addListener();
    }

    private void addListener() {
        mDynamicCommentTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                DynamicCommentActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });

        mDynamicCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
                if (status == 1) {
                    //一级评论
                    //对应动态id
                    int fDId = mDynamicImgBean.getdId();
                    //评论者id
                    int fUId = userInfo.getUid();
                    String fName = userInfo.getNickName();
                    String fUImg = userInfo.getImageUrl();
                    String content = mDynamicCommentFootEdit.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(new Date());
                    List<SecondCommentBean> list = new ArrayList<SecondCommentBean>();
                    CommentBean commentBean = new CommentBean(fDId, fUId, fUImg, fName, content,
                            time,
                            list);
                    mList.add(commentBean);
                    Gson gson = new Gson();
                    String firstComment = gson.toJson(commentBean);
                    OkHttpUtils.post()
                            .url(url)
                            .addParams("appRequest", "AddFirstComment")
                            .addParams("firstComment", firstComment)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e) {

                                }

                                @Override
                                public void onResponse(String response) {

                                }
                            });
                    status = 2;
                    mDynamicImgBean.setCommentCount(mDynamicImgBean.getCommentCount() + 1);
                    mHeaderCommentCount.setText(mDynamicImgBean.getCommentCount() + "");
                } else {
                    //自己的id和名字
                    int uId0 = userInfo.getUid();
                    String uName0 = userInfo.getNickName();
                    //回复内容
                    String content = mDynamicCommentFootEdit.getText().toString();
                    //回复时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(new Date());
                    if (uId0 == (int) mMap.get("uId1")) {
                        new AlertDialog.Builder(DynamicCommentActivity.this)
                                .setTitle("提示")
                                .setMessage("不能回复自己!")
                                .setPositiveButton("确定",null)
                                .show();
                    } else {
                        SecondCommentBean bean = new SecondCommentBean((int) mMap.get("sFCId"),
                                uId0,
                                uName0, (int) mMap.get("uId1"), (String) mMap.get("uName1"),
                                content,
                                time);
                        mList.get((int) mMap.get("position")).getList().add(bean);
                        Gson gson = new Gson();
                        String secondComment = gson.toJson(bean);
                        Log.d("TAG", secondComment);
                        OkHttpUtils.post()
                                .url(url)
                                .addParams("appRequest", "AddSecondComment")
                                .addParams("secondComment", secondComment)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {
                                    }

                                    @Override
                                    public void onResponse(String response) {

                                    }
                                });
                    }
                }
                mAdapter.notifyDataSetChanged();
                mDynamicCommentFootEdit.setText("");
                mDynamicCommentFootEdit.clearFocus();
                mCommentBottomLayout.setVisibility(View.GONE);
            }
        });

        mHeaderCommentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 1;
                mCommentBottomLayout.setVisibility(View.VISIBLE);
                mDynamicCommentFootEdit.requestFocus();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        mDynamicImgBean = (DynamicImgBean) intent.getSerializableExtra("dynamicBean");
        mList = new ArrayList<>();
        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "ShowComment")
                //.addParams("dId", "" + 1)
                .addParams("dId", "" + mDynamicImgBean.getdId())
                .addParams("myId", "" + ((MyApplication) getApplication()).getUserInfo().getUid())
                .build()
                .execute(mCommentCallBack = new CommentCallBack());

        //头部View
        View view = LayoutInflater.from(this).inflate(R.layout.dynamic_comment_img_header,
                mDynamicCommentListView, false);
        HeadInitView(view);
        Glide.with(this)
                .load(mDynamicImgBean.getHeadPhoto())
                //.placeholder(R.drawable.head_photo)
                .transform(new GlideCircleTransform(this))
                .into(mHeaderImg);
        mHeaderName.setText(mDynamicImgBean.getName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = mDynamicImgBean.getTime();
        try {
            Date date = format.parse(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            time = simpleDateFormat.format(date);
            Log.e("TAG+LDD","time"+time+" "+mDynamicImgBean.getTime());
            mHeaderTime.setText(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mHeaderContent.setText(mDynamicImgBean.getContent());
        List<String> imgList = mDynamicImgBean.getImgList();
        List<ImageInfo> infoList = new ArrayList<>();
        for (int i = 0; i < imgList.size(); i++) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setThumbnailUrl(imgList.get(i));
            imageInfo.setBigImageUrl(imgList.get(i));
            infoList.add(imageInfo);
        }
        ClickNineGridViewAdapter adapter = new ClickNineGridViewAdapter(this, infoList);
        mHeaderGridView.setAdapter(adapter);
        mDynamicCommentListView.addHeaderView(view);
        if (mDynamicImgBean.getPraiseStatus() == 1) {
            mHeaderPraiseImg.setImageResource(R.drawable.ic_praise_red);
        }
        mHeaderPraiseCount.setText(mDynamicImgBean.getPraiseCount() + "");
        mHeaderCommentCount.setText(mDynamicImgBean.getCommentCount() + "");
        mHeaderPraiseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDynamicImgBean.getPraiseStatus() == 0) {
                    mHeaderPraiseImg.setImageResource(R.drawable.ic_praise_red);
                    mDynamicImgBean.setPraiseCount(mDynamicImgBean.getPraiseCount() + 1);
                    mHeaderPraiseCount.setText(mDynamicImgBean.getPraiseCount() + "");
                    mDynamicImgBean.setPraiseStatus(1);
                    addPraise(mDynamicImgBean.getdId());
                } else {

                }
            }
        });
        mAdapter = new DynamicCommentItemAdapter(this, mList);
        mDynamicCommentListView.setAdapter(mAdapter);
    }

    private void addPraise(int i) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("pDId", i);
        //用户id
        //map.put("pUId",mMyApplication.getUserInfo().getUid());
        map.put("pUId", 1);
        Gson gson = new Gson();
        String praiseMap = gson.toJson(map);
        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "AddPraise")
                .addParams("praiseMap", praiseMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {

                    }
                });
    }

    //点击文字事件
    @Override
    public void onClick(View view, MySpan span) {
        Toast.makeText(DynamicCommentActivity.this, span.getId() + "：" + span.getContent(),
                Toast.LENGTH_SHORT).show();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getfCId() == span.getBean().getsFCId()) {
                mMap.put("position", i);
                mMap.put("sFCId", span.getBean().getsFCId());
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
        mHeaderGridView = (NineGridView) view.findViewById(R.id.comment_imgHeader_gridView);
        mHeaderPraiseImg = (ImageView) view.findViewById(R.id.comment_imgHeader_praiseImg);
        mHeaderPraiseCount = (TextView) view.findViewById(R.id.comment_imgHeader_praiseCount);
        mHeaderCommentImg = (ImageView) view.findViewById(R.id.comment_imgHeader_commentImg);
        mHeaderCommentCount = (TextView) view.findViewById(R.id.comment_imgHeader_commentCount);
    }

    /**
     * 用来被适配器回调，适配器监听回复操作，一旦回复执行，底部editText获取焦点
     * 同时得到adapter返回的数据然后监听发送按钮刷新数据
     *
     * @param status
     */
    public void getFocus(boolean status) {
        if (status) {
            mCommentBottomLayout.setVisibility(View.VISIBLE);
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
