package com.running.android_main;

import android.content.DialogInterface;
import android.os.Bundle;
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

import com.running.myviews.MyInfoItemView;
import com.running.myviews.TopBar;
import com.running.myviews.TopBar.OnTopbarClickListener;

import java.util.Calendar;

public class MyDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnTopbarClickListener {
    private MyApplication mApplication;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydetails);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        initViews();
        mDialogBuilder = new AlertDialog.Builder(this);
        initListeners();
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
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveinfo:
                //保存用户修改后的信息,提交到服务器
                Toast.makeText(MyDetailsActivity.this, "saveinfo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.details_head:
                Toast.makeText(MyDetailsActivity.this, "修改头像", Toast.LENGTH_SHORT).show();
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
                mMaleRadioButton.setText("帅哥");
                mFemaleRadioButton = new RadioButton(this);
                mFemaleRadioButton.setText("美女");
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

    private void initEditText(String text) {
        //文本显示的位置在EditText的最上方
        mEditText.setGravity(Gravity.TOP);
        //改变默认的单行模式
        mEditText.setSingleLine(false);
        //水平滚动设置为False
        mEditText.setHorizontallyScrolling(false);
        mEditText.setTextSize(20);
        //光标在文字末尾
        mEditText.setSelection(text.length());
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
        mApplication.removeActivity(this);
    }

    @Override
    public void onTopbarRightImageClick(ImageView imageView) {
    }
}
