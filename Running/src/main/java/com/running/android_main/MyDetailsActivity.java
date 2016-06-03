package com.running.android_main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.running.beans.UserInfo;
import com.running.myviews.MyInfoItemView;
import com.running.myviews.TopBar;
import com.running.myviews.TopBar.OnTopbarClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import java.io.File;
import java.util.Calendar;

public class MyDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnTopbarClickListener {
    private MyApplication mApplication;
    private static final String mPath = "http://192.168.191.1:8080/Running/changeUserInfoServlet";
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
    private ProgressDialog mProgressDialog;

    //用户信息
    private UserInfo mUserInfo;
    private RequestQueue requestQueue;
    //修改头像
    private String[] items = new String[]{"选择本地图片", "拍照"};
    /* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    /*头像名称*/
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";

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
                //保存用户修改后的信息
                String u_nickName = mNickItem.getDataText();
                String u_height = mHeightItem.getDataText();
                String u_weight = mWeightItem.getDataText();
                String u_sex = mSexItem.getDataText();
                String u_birthday = mBirthdayItem.getDataText();
                String u_address = mAddressItem.getDataText();
                String u_signature = mSignatureItem.getDataText();
                /*Log.e("my", "u_nickName=" + mUserInfo.getNickName() + ";u_height=" +
                        mUserInfo.getHeight() + "cm" + ";u_weight=" + mUserInfo.getWeight() + "kg"
                        + ";u_sex=" + mUserInfo.getSex() +
                        ";u_birthday=" + mUserInfo.getBirthday() + ";u_address=" + mUserInfo.getAddress() +
                        ";u_signature=" + mUserInfo.getSignature());*/
                //如果信息未改变，则不提交服务器
                if (u_nickName.equals(mUserInfo.getNickName()) &&
                        u_height.equals(mUserInfo.getHeight() + "cm") &&
                        u_weight.equals(mUserInfo.getWeight() + "kg") &&
                        u_sex.equals(mUserInfo.getSex()) &&
                        u_birthday.equals(mUserInfo.getBirthday()) &&
                        u_address.equals(mUserInfo.getAddress()) &&
                        u_signature.equals(mUserInfo.getSignature())) {
                    Toast.makeText(MyDetailsActivity.this, "个人信息未发生变化", Toast.LENGTH_SHORT).show();
                } else {
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
                mDatePicker.init(2000,
                        mCalendar.get(Calendar.MONTH) + 1,
                        mCalendar.get(Calendar.DAY_OF_MONTH),
                        null);
                mDialogBuilder.setView(mDatePicker);
                showAlertDialog();
                break;
            case R.id.address_item:
                mInfoItemView = (MyInfoItemView) v;
                mEditText = new EditText(this);
                String address = mInfoItemView.getDataText();
                mEditText.setText(address);
                initEditText(address);
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

                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (hasSdcard()) {
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(),
                                                    IMAGE_FILE_NAME)));
                                }

                                startActivityForResult(intentFromCapture,
                                        CAMERA_REQUEST_CODE);
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
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory()
                                        + "/" + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(MyDetailsActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
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
        }
    }


    private void saveUserInfo(String u_nickName, String u_height, String u_weight, String u_sex,
                              String u_birthday, String u_address, String u_signature) {
        mProgressDialog = ProgressDialog.show(this, "请等待...", "正在提交信息...");
        requestQueue = NoHttp.newRequestQueue(1);
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
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
            if (what == WHAT) {
                String result = response.get();
                UserInfo userinfo = new Gson().fromJson(result, UserInfo.class);
                if (userinfo.getCode().equals("1")) {
                    Toast.makeText(MyDetailsActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    mApplication.setUserInfo(userinfo);
                } else {
                    Toast.makeText(MyDetailsActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mProgressDialog.dismiss();
            Toast.makeText(MyDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
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
        mEditText.setBackgroundResource(R.drawable.editbox_background_focus_yellow);
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
                        mInfoItemView.setDataText(mEditText.getText().toString());
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
        this.finish();
        MyDetailsActivity.this.finish();
    }

    @Override
    public void onTopbarRightImageClick(ImageView imageView) {
    }
}
