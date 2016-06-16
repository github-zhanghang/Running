package com.running.android_main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.running.beans.City;
import com.running.beans.District;
import com.running.beans.Provence;
import com.running.beans.UserInfo;
import com.running.myviews.CustomProgressDialog;
import com.running.myviews.MyInfoItemView;
import com.running.myviews.TopBar;
import com.running.myviews.TopBar.OnTopbarClickListener;
import com.running.utils.GetQiNiuYunToken;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;

public class MyDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnTopbarClickListener {
    private MyApplication mApplication;
    private static final String mPath = MyApplication.HOST + "changeUserInfoServlet";
    private static final int WHAT = 1;

    private TopBar mTopBar;
    private View mDetailsHeadView;
    private ImageView mUserImage;
    private TextView mUserAccount;

    private MyInfoItemView mNickItem, mHeightItem, mWeightItem,
            mSexItem, mBirthdayItem, mAddressItem, mSignatureItem;
    private Button mSaveInfoButton;

    //弹框内容
    private MyInfoItemView mInfoItemView;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mAlertDialog;
    private EditText mEditText;
    private DatePicker mDatePicker;
    private Calendar mCalendar;
    private RadioGroup mRadioGroup;
    private RadioButton mMaleRadioButton, mFemaleRadioButton;

    //等待框
    private CustomProgressDialog mProgressDialog;

    //用户信息
    private UserInfo mUserInfo;
    private RequestQueue requestQueue;
    //修改头像
    private String[] items = new String[]{"选择本地图片", "拍照"};
    /* 请求码*/
    private final int IMAGE_REQUEST_CODE = 0;
    private final int CAMERA_REQUEST_CODE = 1;
    private final int RESULT_REQUEST_CODE = 2;
    /*头像名称*/
    private final String IMAGE_FILE_NAME = "head.jpg";
    private Toast mToast;

    //省市区三级联动
    private List<Provence> provences;
    private Provence provence;
    ArrayAdapter<Provence> adapter01;
    ArrayAdapter<City> adapter02;
    ArrayAdapter<District> adapter03;
    private Spinner spinner01, spinner02, spinner03;

    //头像上传
    GetQiNiuYunToken mGetQiNiuYunToken;
    String token;
    //七牛云外链域名
    String NET_PATH = "o8hzh2lfo.bkt.clouddn.com";
    //七牛云上传空间名
    String UPLOAD_SPACE_NAME = "running";
    //图片本地路径
    private String mImageLocalPath;
    //图片网址
    private String mImageUrl;

    //个人信息
    private String u_nickName, u_height, u_weight, u_sex, u_birthday, u_address, u_signature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydetails);
        mApplication = (MyApplication) getApplication();
        mUserInfo = mApplication.getUserInfo();
        initViews();
        initData();
        mDialogBuilder = new AlertDialog.Builder(this);
        initListeners();
    }

    private void initData() {
        //加载头像
        Glide.with(MyDetailsActivity.this)
                .load(mUserInfo.getImageUrl())
                .error(R.drawable.fail)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(mUserImage);
        mUserAccount.setText(mUserInfo.getAccount());
        mNickItem.setDataText(mUserInfo.getNickName());
        mHeightItem.setDataText(mUserInfo.getHeight() + "cm");
        mWeightItem.setDataText(mUserInfo.getWeight() + "kg");
        mSexItem.setDataText(mUserInfo.getSex());
        mBirthdayItem.setDataText(mUserInfo.getBirthday());
        mAddressItem.setDataText(mUserInfo.getAddress());
        mSignatureItem.setDataText(mUserInfo.getSignature());
    }

    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.details_topbar);

        mDetailsHeadView = findViewById(R.id.details_head);
        mUserImage = (ImageView) mDetailsHeadView.findViewById(R.id.details_uimg);
        mUserAccount = (TextView) mDetailsHeadView.findViewById(R.id.details_uaccount);

        mNickItem = (MyInfoItemView) findViewById(R.id.nick_item);
        mHeightItem = (MyInfoItemView) findViewById(R.id.height_item);
        mWeightItem = (MyInfoItemView) findViewById(R.id.weight_item);
        mSexItem = (MyInfoItemView) findViewById(R.id.sex_item);
        mBirthdayItem = (MyInfoItemView) findViewById(R.id.birth_item);
        mAddressItem = (MyInfoItemView) findViewById(R.id.address_item);
        mSignatureItem = (MyInfoItemView) findViewById(R.id.signature_item);
        mSaveInfoButton = (Button) findViewById(R.id.saveinfo);
    }

    private void initListeners() {
        mTopBar.setOnTopbarClickListener(this);
        mDetailsHeadView.setOnClickListener(this);
        mNickItem.setOnClickListener(this);
        mHeightItem.setOnClickListener(this);
        mWeightItem.setOnClickListener(this);
        mSexItem.setOnClickListener(this);
        mBirthdayItem.setOnClickListener(this);
        mAddressItem.setOnClickListener(this);
        mSignatureItem.setOnClickListener(this);
        mSaveInfoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveinfo:
                //如果信息未改变，则不提交服务器
                if (!isChanged()) {
                    Toast.makeText(MyDetailsActivity.this, "个人信息未发生变化", Toast.LENGTH_SHORT).show();
                } else {
                    io.rong.imlib.model.UserInfo userInfo =
                            new io.rong.imlib.model.UserInfo(mApplication.getUserInfo().getAccount(),
                                    u_nickName, Uri.parse(mApplication.getUserInfo().getImageUrl()));
                    RongContext.getInstance().getUserInfoCache().
                            put(mApplication.getUserInfo().getAccount(), userInfo);
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    saveUserInfo(u_nickName, u_height, u_weight, u_sex, u_birthday, u_address, u_signature);
                }
                break;
            case R.id.details_head:
                Toast.makeText(MyDetailsActivity.this, "修改头像", Toast.LENGTH_SHORT).show();
                showChangeImageDialog();
                break;
            case R.id.nick_item:
                mInfoItemView = (MyInfoItemView) v;
                mEditText = new EditText(this);
                String nick = mInfoItemView.getDataText();
                mEditText.setText(nick);
                //最多输入6个字符
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                initEditText(nick);
                break;
            case R.id.height_item:
                mInfoItemView = (MyInfoItemView) v;
                mEditText = new EditText(this);
                String height = mInfoItemView.getDataText();
                height = height.substring(0, height.length() - 2);
                mEditText.setText(height);
                //最多输入3个字符
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                initEditText(height);
                break;
            case R.id.weight_item:
                mInfoItemView = (MyInfoItemView) v;
                mEditText = new EditText(this);
                String weight = mInfoItemView.getDataText();
                weight = weight.substring(0, weight.length() - 2);
                mEditText.setText(weight);
                //最多输入3个字符
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                initEditText(weight);
                break;
            case R.id.sex_item:
                mInfoItemView = (MyInfoItemView) v;
                mRadioGroup = new RadioGroup(this);
                mRadioGroup.setOrientation(LinearLayout.VERTICAL);
                mRadioGroup.setGravity(Gravity.CENTER);
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
            case R.id.birth_item:
                mInfoItemView = (MyInfoItemView) v;
                // 获取日历对象
                mCalendar = Calendar.getInstance();
                mDatePicker = new DatePicker(this);
                mDatePicker.setCalendarViewShown(false);
                mDatePicker.init(2000,
                        mCalendar.get(Calendar.MONTH) + 1,
                        mCalendar.get(Calendar.DAY_OF_MONTH),
                        null);
                mDialogBuilder.setView(mDatePicker);
                showAlertDialog();
                break;
            case R.id.address_item:
                mInfoItemView = (MyInfoItemView) v;
                View view = LayoutInflater.from(MyDetailsActivity.this).inflate(R.layout.city, null);
                initSpinner(view);
                mDialogBuilder.setView(view);
                showAlertDialog();
                break;
            case R.id.signature_item:
                mInfoItemView = (MyInfoItemView) v;
                mEditText = new EditText(this);
                String signature = mInfoItemView.getDataText();
                mEditText.setText(signature);
                //最多输入30个字符
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                initEditText(signature);
                break;
        }
    }

    private void initSpinner(View view) {
        spinner01 = (Spinner) view.findViewById(R.id.spinner01);
        spinner02 = (Spinner) view.findViewById(R.id.spinner02);
        spinner03 = (Spinner) view.findViewById(R.id.spinner03);

        try {
            provences = getProvinces();
        } catch (XmlPullParserException e) {
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
                adapter02 = new ArrayAdapter<City>(MyDetailsActivity.this,
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
                adapter03 = new ArrayAdapter<District>(MyDetailsActivity.this,
                        android.R.layout.simple_list_item_1, provence.getCitys().get(position)
                        .getDistricts());
                spinner03.setAdapter(adapter03);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    private void showChangeImageDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery
                                        .setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery,
                                        IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (hasSdcard()) {
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(),
                                                    IMAGE_FILE_NAME)));
                                }
                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    //检查是否存在SD卡
    private boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于操作取消时候
        if (resultCode != RESULT_CANCELED)
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (hasSdcard()) {
                        mImageLocalPath = Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME;
                        File tempFile = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        showToast("未找到存储卡，无法存储照片！");
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            mUserImage.setImageDrawable(drawable);
            //图片保存在本地
            saveBitmap(photo);
            mProgressDialog = new CustomProgressDialog(this, "正在上传...", R.drawable.frame);
            mProgressDialog.show();
            //上传图片
            upload(mImageLocalPath);
        }
    }

    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bm) {
        if (hasSdcard()) {
            mImageLocalPath = Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + IMAGE_FILE_NAME;
        } else {
            showToast("未找到存储卡，无法存储照片！");
        }
        File f = new File(mImageLocalPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserInfo(String u_nickName, String u_height, String u_weight, String u_sex,
                              String u_birthday, String u_address, String u_signature) {
        mProgressDialog = new CustomProgressDialog(this, "正在提交...", R.drawable.frame);
        mProgressDialog.show();
        requestQueue = NoHttp.newRequestQueue(1);
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "userinfo");
        request.add("account", mUserInfo.getAccount());
        request.add("nickname", u_nickName);
        request.add("height", u_height.substring(0, u_height.length() - 2));
        request.add("weight", u_weight.substring(0, u_weight.length() - 2));
        request.add("sex", u_sex);
        request.add("birthday", u_birthday);
        request.add("address", u_address);
        request.add("signature", u_signature);
        requestQueue.add(WHAT, request, onResponseListener);
        requestQueue.start();
    }

    private OnResponseListener onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            mProgressDialog.dismiss();
            String result = response.get();
            Log.e("my", "details.result=" + result);
            if (what == WHAT) {
                UserInfo userinfo = new Gson().fromJson(result, UserInfo.class);
                if (userinfo.getCode().equals("1")) {
                    showToast("修改头像成功");
                    mApplication.setUserInfo(userinfo);
                } else {
                    showToast("修改头像失败");
                }
            } else if (what == 2) {
                if (result.equals("1")) {
                    mApplication.getUserInfo().setImageUrl(mImageUrl);
                    io.rong.imlib.model.UserInfo userInfo =
                            new io.rong.imlib.model.UserInfo(
                                    mApplication.getUserInfo().getAccount(),
                                    mApplication.getUserInfo().getNickName(),
                                    Uri.parse(mApplication.getUserInfo().getImageUrl()));
                    RongContext.getInstance().getUserInfoCache().
                            put(mApplication.getUserInfo().getAccount(), userInfo);
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                } else if (result.equals("0")) {
                    showToast("修改头像失败");
                } else {
                    showToast("修改头像失败");
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mProgressDialog.dismiss();
            showToast("error");
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void initEditText(String text) {
        //文本显示的位置在EditText的最上方
        mEditText.setGravity(Gravity.TOP);
        //改变默认的单行模式
        mEditText.setSingleLine(false);
        //水平滚动设置为False
        mEditText.setHorizontallyScrolling(false);
        mEditText.setTextSize(20);
        if (text != null && !text.equals("")) {
            //光标在文字末尾
            mEditText.setSelection(text.length());
        }
        mEditText.setBackgroundResource(R.drawable.ic_editext);
        mDialogBuilder.setView(mEditText);
        showAlertDialog();
    }

    private void showAlertDialog() {
        mDialogBuilder.setTitle("修改" + mInfoItemView.getLabelText());
        mDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mInfoItemView.getId()) {
                    case R.id.nick_item:
                        mInfoItemView.setDataText(mEditText.getText().toString());
                        break;
                    case R.id.height_item:
                        mInfoItemView.setDataText(mEditText.getText().toString() + "cm");
                        break;
                    case R.id.weight_item:
                        mInfoItemView.setDataText(mEditText.getText().toString() + "kg");
                        break;
                    case R.id.sex_item:
                        mInfoItemView.setDataText(mMaleRadioButton.isChecked() ?
                                mMaleRadioButton.getText().toString() :
                                mFemaleRadioButton.getText().toString());
                        break;
                    case R.id.birth_item:
                        mInfoItemView.setDataText(mDatePicker.getYear() + "-"
                                + (mDatePicker.getMonth() + 1) + "-"
                                + mDatePicker.getDayOfMonth());
                        break;
                    case R.id.address_item:
                        mInfoItemView.setDataText(spinner01.getSelectedItem().toString() + " "
                                + spinner02.getSelectedItem().toString() + " "
                                + spinner03.getSelectedItem().toString());
                        break;
                    case R.id.signature_item:
                        mInfoItemView.setDataText(mEditText.getText().toString());
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

    @Override
    public void onTopbarLeftImageClick(ImageView imageView) {
        onMyBack();
    }

    @Override
    public void onTopbarRightImageClick(ImageView imageView) {
    }

    /**
     * 上传图片到七牛云
     *
     * @param path (图片本地路径)
     */
    public void upload(String path) {
        UploadManager uploadManager = new UploadManager();
        mGetQiNiuYunToken = new GetQiNiuYunToken();
        token = mGetQiNiuYunToken.getToken(UPLOAD_SPACE_NAME);
        final String imgName = mApplication.getUserInfo().getAccount()
                + System.currentTimeMillis() + ".jpg";
        uploadManager.put(path, imgName, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        //info.statusCode 回掉状态码
                        if (info.statusCode == 200) {
                            mImageUrl = "http://" + NET_PATH + File.separator + imgName;
                            //修改数据库头像地址
                            updateUserImage();
                        } else {
                            mProgressDialog.dismiss();
                            showToast("上传失败");
                        }
                    }
                }, null);
    }

    private void updateUserImage() {
        requestQueue = NoHttp.newRequestQueue(1);
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "changeImage");
        request.add("account", mUserInfo.getAccount());
        request.add("url", mImageUrl);
        requestQueue.add(2, request, onResponseListener);
        requestQueue.start();
    }

    public void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MyDetailsActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public boolean isChanged() {
        UserInfo userInfo = mApplication.getUserInfo();
        //保存用户修改后的信息
        u_nickName = mNickItem.getDataText() + "";
        u_height = mHeightItem.getDataText() + "";
        u_weight = mWeightItem.getDataText() + "";
        u_sex = mSexItem.getDataText() + "";
        u_birthday = mBirthdayItem.getDataText() + "";
        u_address = mAddressItem.getDataText() + "";
        u_signature = mSignatureItem.getDataText() + "";
        //信息未改变
        if (u_nickName.equals(userInfo.getNickName() + "") &&
                u_height.equals(userInfo.getHeight() + "cm") &&
                u_weight.equals(userInfo.getWeight() + "kg") &&
                u_sex.equals(userInfo.getSex() + "") &&
                u_birthday.equals(userInfo.getBirthday() + "") &&
                u_address.equals(userInfo.getAddress() + "") &&
                u_signature.equals(userInfo.getSignature() + "")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        onMyBack();
    }

    public void onMyBack() {
        if (!isChanged()) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this).setTitle("信息未保存，是否退出？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyDetailsActivity.this.finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
}
