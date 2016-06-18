package com.running.android_main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.running.adapters.GoodsSearchAdapter;
import com.running.beans.GoodsData;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class GoodsSearchActivity extends AppCompatActivity {

    private TopBar mTopBar;
    SearchView goodsSearchView;
    ListView goodsListView;

    List<GoodsData> mGoodsDataList=new ArrayList<>();
    GoodsSearchAdapter mGoodsSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_search);

        initView();
        initData();

        mGoodsSearchAdapter=new GoodsSearchAdapter(this,mGoodsDataList);
        goodsListView.setAdapter(mGoodsSearchAdapter);
        goodsListView.setVisibility(View.GONE);

        addTextListener();
        addClickListener();

        addBackListener();
    }

    private void initView() {
        mTopBar= (TopBar) findViewById(R.id.searchgoods_topbar);

        goodsSearchView= (SearchView) findViewById(R.id.goodsSearchView);
        // 设置该SearchView默认是否自动缩小为图标
        goodsSearchView.setIconified(false);
        // 设置该SearchView内默认显示的提示文本
        goodsSearchView.setQueryHint("查找");
       /* //设置显示提交按钮
        goodsSearchView.setSubmitButtonEnabled(true);*/

        goodsListView= (ListView) findViewById(R.id.goodsListview);
        //设置lv可以被过虑
        goodsListView.setTextFilterEnabled(true);
    }

    private void initData() {
        OkHttpUtils.get()
                .url(MyApplication.HOST+"goodsSearchServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String name=jsonObject.getString("name");
                                double price=jsonObject.getDouble("price");
                                String img=jsonObject.getString("img");
                                String  html=jsonObject.getString("html");
                                GoodsData goodsData=new GoodsData(name,price,img,html);
                                mGoodsDataList.add(goodsData);
                            }
                            mGoodsSearchAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }                    }
                });
    }

    private void addTextListener() {

        //点击关闭时listview不可见
        goodsSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                goodsListView.setVisibility(View.GONE);
                return false;
            }
        });
        //监听文字变化
        goodsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // 单击搜索按钮时激发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 实际应用中应该在该方法内执行实际查询
                // 此处仅使用Toast显示用户输入的查询内容
                //Toast.makeText(GoodsSearchActivity.this, "您输入的是:" + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            // 用户输入字符时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(GoodsSearchActivity.this,"textchange->"+newText,Toast.LENGTH_SHORT).show();

                List<GoodsData> mSearchList=new ArrayList<>();
                for (int i = 0; i < mGoodsDataList.size(); i++) {
                    int index= mGoodsDataList.get(i).getName().indexOf(newText);
                    //存在匹配的数据
                    if (index!=-1){
                        mSearchList.add(mGoodsDataList.get(i));
                    }
                }
                mGoodsSearchAdapter=new GoodsSearchAdapter(GoodsSearchActivity.this,mSearchList);
                goodsListView.setAdapter(mGoodsSearchAdapter);
                goodsListView.setVisibility(View.VISIBLE);
                if (newText.isEmpty()){
                    goodsListView.setVisibility(View.GONE);
                }
                return false;
            }

        });
    }

    private void addClickListener() {
        //item点击事件
        goodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Bundle bundle=new Bundle();
                GoodsData goodsData= (GoodsData) mGoodsSearchAdapter.getItem(i);
                bundle.putString("weburl",goodsData.getHtml());
                //Toast.makeText(GoodsSearchActivity.this,goodsData.getName() , Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(GoodsSearchActivity.this, GoodsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }

    private void addBackListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                GoodsSearchActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }
}
