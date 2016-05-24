package com.running.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.running.adapters.XiaoxiFragmentAdapter;
import com.running.android_main.MainActivity;
import com.running.android_main.R;
import com.running.myviews.TopBar;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by mhdong on 2016/4/29.
 */
public class XiaoxiFragment extends Fragment {
    private MainActivity mActivity;
    private View mView;
    private ViewPager mViewPager;
    private List<Fragment> mList;
    private XiaoxiFragmentAdapter mAdapter;
    private FragmentManager mFragmentManager;
    private XiaoxiRightFragment mXiaoxiRightFragment;

    private TopBar mTopBar;


    private Fragment mConversationList;
    private Fragment mConversationFragment = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.xiaoxi, null);
        initViews();
        initFragments();
        initViewPager();
        setListeners();
        return mView;
    }

    private void initViews() {
        mActivity = (MainActivity) getActivity();
        mTopBar = (TopBar) mView.findViewById(R.id.xiaoxi_topbar);
    }

    private void setListeners() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mTopBar.setLeftTextColor(Color.WHITE);
                        mTopBar.setRightTextColor(Color.GRAY);
                        break;
                    case 1:
                        mTopBar.setLeftTextColor(Color.GRAY);
                        mTopBar.setRightTextColor(Color.WHITE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                Toast.makeText(mActivity, "添加好友", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViewPager() {
        mViewPager = (ViewPager) mView.findViewById(R.id.xiaoxiViewpager);
        mAdapter = new XiaoxiFragmentAdapter(mFragmentManager, mList);
        mViewPager.setAdapter(mAdapter);
    }

    private void initFragments() {
        mList = new ArrayList<>();
        mFragmentManager = mActivity.getSupportFragmentManager();
        //获取融云会话列表的对象
        mConversationList = initConversationList();
        mXiaoxiRightFragment = new XiaoxiRightFragment();
        mList.add(mConversationList);
        mList.add(mXiaoxiRightFragment);
    }

    private Fragment initConversationList() {
        Fragment fragment = null;
        if (mConversationFragment ==  null){
            ConversationListFragment listFragment = ConversationListFragment.getInstance();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                    .build();
            listFragment.setUri(uri);
            fragment = listFragment;
        }else {
            fragment = mConversationFragment;
        }
        return fragment;
    }
}
