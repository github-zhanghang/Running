package com.running.android_main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.beans.City;
import com.running.beans.District;
import com.running.beans.NearUserInfo;
import com.running.beans.Provence;
import com.running.beans.UserInfo;
import com.running.myviews.MyInfoItemView;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SearchConditionsActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String SearchConditions = MyApplication.HOST + "SearchConditionServlet";
    private List<UserInfo> mUserInfoList;
    private TopBar mTopBar;
    private View mSexItem,mAgeItem,mAddressItem;
    private TextView mSexItemTextView,mAgeItemTextView,mAddressItemTextView;
    private Button mSearchButton;
    //弹框内容
    private View mView;
    private String title;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mAlertDialog;
    private RadioGroup mRadioGroup;
    private RadioButton mMaleRadioButton, mFemaleRadioButton;
    private RadioButton mRadioButton1,mRadioButton2,mRadioButton3,mRadioButton4;


    //省市区三级联动
    private List<Provence> provences;
    private Provence provence;
    ArrayAdapter<Provence> adapter01;
    ArrayAdapter<City> adapter02;
    ArrayAdapter<District> adapter03;
    private Spinner spinner01, spinner02, spinner03;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_conditions);
        initViews();
        initListener();
    }


    private void initViews() {
        mDialogBuilder = new AlertDialog.Builder(this);
        mSearchButton = (Button) findViewById(R.id.searchUser);
        mTopBar = (TopBar) findViewById(R.id.search_topbar);
        mUserInfoList = new ArrayList<>();
        mSexItem = findViewById(R.id.sex_itemView);
        mAgeItem = findViewById(R.id.age_itemView);
        mAddressItem = findViewById(R.id.address_itemView);
        mSexItemTextView = (TextView) findViewById(R.id.sex_itemView_tv);
        mAgeItemTextView = (TextView) findViewById(R.id.age_itemView_tv);
        mAddressItemTextView = (TextView) findViewById(R.id.address_itemView_tv);
    }


    private void initListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                SearchConditionsActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });

        mSearchButton.setOnClickListener(this);

        mSexItem.setOnClickListener(this);
        mAgeItem.setOnClickListener(this);
        mAddressItem.setOnClickListener(this);
    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //获取性别
            case R.id.sex_itemView:
                mView =  v;
                title = "选择性别";
                mRadioGroup = new RadioGroup(this);
                mRadioGroup.setOrientation(LinearLayout.VERTICAL);
                mMaleRadioButton = new RadioButton(this);
                mMaleRadioButton.setText("男");
                mFemaleRadioButton = new RadioButton(this);
                mFemaleRadioButton.setText("女");
                mRadioGroup.addView(mMaleRadioButton);
                mRadioGroup.addView(mFemaleRadioButton);
                mMaleRadioButton.setChecked(true);
                mDialogBuilder.setView(mRadioGroup);
                showAlertDialog();
                break;
            //获取年龄区间
            case R.id.age_itemView:
                mView =  v;
                title = "选择年龄范围";
                mRadioGroup = new RadioGroup(this);
                mRadioGroup.setOrientation(LinearLayout.VERTICAL);
                mRadioButton1 = new RadioButton(this);
                mRadioButton1.setText("18岁以下");
                mRadioButton2 = new RadioButton(this);
                mRadioButton2.setText("19岁-26岁");
                mRadioButton3 = new RadioButton(this);
                mRadioButton3.setText("27岁-35岁");
                mRadioButton4 = new RadioButton(this);
                mRadioButton4.setText("35岁以上");
                mRadioGroup.addView(mRadioButton1);
                mRadioGroup.addView(mRadioButton2);
                mRadioGroup.addView(mRadioButton3);
                mRadioGroup.addView(mRadioButton4);
                mRadioButton1.setChecked(true);
                mDialogBuilder.setView(mRadioGroup);
                showAlertDialog();
                break;
            //获取地区
            case R.id.address_itemView:
                mView =  v;
                title = "选择地区";
                View view = LayoutInflater.from(SearchConditionsActivity.this).inflate(R.layout.city, null);
                initSpinner(view);
                mDialogBuilder.setView(view);
                showAlertDialog();
                break;
            case R.id.searchUser:
                String gender = mSexItemTextView.getText().toString();
                String age = mAgeItemTextView.getText().toString().substring(0,2);
                String address = mAddressItemTextView.getText().toString();
                /*Log.e("Ezio123", gender );
                Log.e("Ezio123", age );
                Log.e("Ezio123", address );*/
                request(gender, age, address);
                break;
        }

    }

    private void showAlertDialog() {
        mDialogBuilder.setTitle(title);
        mDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mView.getId()) {
                    case R.id.sex_itemView:

                        mSexItemTextView.setText(mMaleRadioButton.isChecked() ?
                                mMaleRadioButton.getText().toString() :
                                mFemaleRadioButton.getText().toString());
                        break;
                    case R.id.age_itemView:

                        if (mRadioButton1.isChecked()){

                            mAgeItemTextView.setText(mRadioButton1.getText());
                        }else if (mRadioButton2.isChecked()){
                            mAgeItemTextView.setText(mRadioButton2.getText());
                        }else if (mRadioButton3.isChecked()){
                            mAgeItemTextView.setText(mRadioButton3.getText());
                        }else {
                            mAgeItemTextView.setText(mRadioButton4.getText());
                        }
                        break;
                    case R.id.address_itemView:

                        String address = spinner01.getSelectedItem().toString() + " "
                                + spinner02.getSelectedItem().toString() + " "
                                + spinner03.getSelectedItem().toString();
                        mAddressItemTextView.setText(address);

                        break;
                }
            }
        });

        mDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mAlertDialog = mDialogBuilder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }




    private void request(String gender, String age, String address) {
        OkHttpUtils.post()
                .url(SearchConditions)
                .addParams("meid",((MyApplication) getApplication()).getUserInfo().getUid()+"")//用户id
                .addParams("sex", gender)
                .addParams("age", age)
                .addParams("address", address)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("SearchConditions", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("Ezio123", "SearchConditionsActivity: " + response);
                        if (response.equals("[]")){
                            Toast.makeText(SearchConditionsActivity.this, "查找无结果", Toast.LENGTH_SHORT).show();
                        }else {
                            mUserInfoList.clear();
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }




    private void initSpinner(View view) {
        spinner01 = (Spinner) view.findViewById(R.id.spinner01);
        spinner02 = (Spinner) view.findViewById(R.id.spinner02);
        spinner03 = (Spinner) view.findViewById(R.id.spinner03);

        try {
            provences = getProvinces();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        adapter01 = new ArrayAdapter<Provence>(this,
                android.R.layout.simple_list_item_1, provences);
        spinner01.setAdapter(adapter01);
        spinner01.setSelection(0, true);

        adapter02 = new ArrayAdapter<City>(this,
                android.R.layout.simple_list_item_1, provences.get(0)
                .getCitys());
        spinner02.setAdapter(adapter02);
        spinner02.setSelection(0, true);

        adapter03 = new ArrayAdapter<District>(this,
                android.R.layout.simple_list_item_1, provences.get(0)
                .getCitys().get(0).getDistricts());
        spinner03.setAdapter(adapter03);
        spinner03.setSelection(0, true);

        spinner01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                provence = provences.get(position);
                adapter02 = new ArrayAdapter<City>(SearchConditionsActivity.this,
                        android.R.layout.simple_list_item_1, provences.get(
                        position).getCitys());
                spinner02.setAdapter(adapter02);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                adapter03 = new ArrayAdapter<District>(SearchConditionsActivity.this,
                        android.R.layout.simple_list_item_1, provence.getCitys().get(position)
                        .getDistricts());
                spinner03.setAdapter(adapter03);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public List<Provence> getProvinces() throws XmlPullParserException,
            IOException {
        List<Provence> provinces = null;
        Provence province = null;
        List<City> citys = null;
        City city = null;
        List<District> districts = null;
        District district = null;
        Resources resources = getResources();

        InputStream in = resources.openRawResource(R.raw.citys_weather);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(in, "utf-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    provinces = new ArrayList<Provence>();
                    break;
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();
                    if ("p".equals(tagName)) {
                        province = new Provence();
                        citys = new ArrayList<City>();
                        int count = parser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);
                            if ("p_id".equals(attrName))
                                province.setId(attrValue);
                        }
                    }
                    if ("pn".equals(tagName)) {
                        province.setName(parser.nextText());
                    }
                    if ("c".equals(tagName)) {
                        city = new City();
                        districts = new ArrayList<District>();
                        int count = parser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);
                            if ("c_id".equals(attrName))
                                city.setId(attrValue);
                        }
                    }
                    if ("cn".equals(tagName)) {
                        city.setName(parser.nextText());
                    }
                    if ("d".equals(tagName)) {
                        district = new District();
                        int count = parser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);
                            if ("d_id".equals(attrName))
                                district.setId(attrValue);
                        }
                        district.setName(parser.nextText());
                        districts.add(district);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("c".equals(parser.getName())) {
                        city.setDistricts(districts);
                        citys.add(city);
                    }
                    if ("p".equals(parser.getName())) {
                        province.setCitys(citys);
                        provinces.add(province);
                    }

                    break;

            }
            event = parser.next();

        }
        return provinces;
    }

}
