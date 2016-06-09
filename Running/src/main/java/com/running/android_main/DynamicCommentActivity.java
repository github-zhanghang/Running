package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.running.adapters.DynamicCommentItemAdapter;
import com.running.adapters.DynamicImgGridViewAdapter;
import com.running.beans.FirstCommentBean;
import com.running.beans.SecondCommentBean;
import com.running.myviews.MyGridView;
import com.running.myviews.TopBar;
import com.running.utils.MySpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DynamicCommentActivity extends AppCompatActivity implements MySpan.OnClickListener {

    private TextView mCommentImgHeaderPraiseCount;
    private ImageView mHeaderImg;
    private TextView mHeaderName;
    private TextView mHeaderTime;
    private TextView mHeaderContent;
    private MyGridView mHeaderGridView;
    private ImageView mHeaderPraiseImg;
    private TextView mHeaderPraiseCount;
    private List<HashMap<String, Object>> mList;
    private DynamicCommentItemAdapter mAdapter;

    @Bind(R.id.dynamic_comment_topBar)
    TopBar mDynamicCommentTopBar;
    @Bind(R.id.dynamic_comment_listView)
    ListView mDynamicCommentListView;
    @Bind(R.id.dynamic_comment_footEdit)
    EditText mDynamicCommentFootEdit;
    @Bind(R.id.dynamic_comment_button)
    Button mDynamicCommentButton;

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
    }

    private void initData() {
        mList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        //添加一级评论
        FirstCommentBean firstCommentBean = new FirstCommentBean(1, 0, "张三", "吃了没",
                "04-07 10:12");
        map.put("firstComment", firstCommentBean);
        //对应一级评论下的二级评论
        List<SecondCommentBean> secondCommentList = new ArrayList<>();
        SecondCommentBean secondCommentBean = new SecondCommentBean(0, 1, "李四", "张三",
                "吃了你呢？", "04-07 10:20");
        secondCommentList.add(secondCommentBean);
        secondCommentBean = new SecondCommentBean(1, 0, "张三", "李四", "我也吃了。", "04-07 10:40");
        secondCommentList.add(secondCommentBean);
        secondCommentBean = new SecondCommentBean(0, 1, "李四", "张三", "一起玩去。", "04-07 11:08");
        secondCommentList.add(secondCommentBean);
        secondCommentBean = new SecondCommentBean(1, 0, "张三", "李四", "好的。", "04-07 11:40");
        secondCommentList.add(secondCommentBean);
        map.put("secondComment", secondCommentList);
        mList.add(map);

        map = new HashMap<>();
        firstCommentBean = new FirstCommentBean(3, 0, "王五", "吃了没", "04-07 10:13");
        map.put("firstComment", firstCommentBean);
        secondCommentList = new ArrayList<>();
        secondCommentBean = new SecondCommentBean(0, 3, "李四", "王五", "吃了你呢？", "04-07 10:20");
        secondCommentList.add(secondCommentBean);
        secondCommentBean = new SecondCommentBean(3, 0, "王五", "李四", "我也吃了。", "04-07 10:40");
        secondCommentList.add(secondCommentBean);
        secondCommentBean = new SecondCommentBean(0, 3, "李四", "王五", "一起玩去。", "04-07 11:08");
        secondCommentList.add(secondCommentBean);
        map.put("secondComment", secondCommentList);
        mList.add(map);

        //头部View
        View view = LayoutInflater.from(this).inflate(R.layout.dynamic_comment_img_header,
                mDynamicCommentListView, false);
        HeadInitView(view);
        mHeaderImg.setImageResource(R.drawable.head_photo);
        mHeaderName.setText("无名");
        mHeaderTime.setText("04-07 07:05");
        mHeaderContent.setText("四月，你好");
        List<String> imgList = new ArrayList<>();
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
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
    }

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
}
