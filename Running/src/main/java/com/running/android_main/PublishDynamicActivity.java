package com.running.android_main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.running.adapters.DynamicPublishGridViewAdapter;
import com.running.myviews.MyGridView;
import com.running.myviews.TopBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class PublishDynamicActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private List<Object> mList;
    private ArrayList<String> selectPhotos;
    private DynamicPublishGridViewAdapter mAdapter;

    private PopupWindow mPopupWindow;
    private Uri mUri;
    private String path;

    @Bind(R.id.dynamic_publish_topBar)
    TopBar mDynamicPublishTopBar;
    @Bind(R.id.dynamic_publish_content)
    EditText mDynamicPublishContent;
    @Bind(R.id.dynamic_publish_gridView)
    MyGridView mDynamicPublishGridView;
    @Bind(R.id.dynamic_publish_location)
    TextView mDynamicPublishLocation;
    @Bind(R.id.dynamic_publish_location_button)
    ImageButton mDynamicPublishLocationButton;
    @Bind(R.id.dynamic_publish_publish_button)
    Button mDynamicPublishPublishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_publish);
        ButterKnife.bind(this);
        addImg();
        mAdapter = new DynamicPublishGridViewAdapter(this, mList, mDynamicPublishGridView);
        mDynamicPublishGridView.setAdapter(mAdapter);
        mDynamicPublishGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == (mList.size() - 1)) {
                    showPopupWindow();
                    mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                } else {
                    Intent intent = new Intent(PublishDynamicActivity.this,
                            PhotoPagerActivity.class);
                    intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                    intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, selectPhotos);
                    intent.putExtra(PhotoPagerActivity.EXTRA_SHOW_DELETE, true);
                    startActivityForResult(intent, CHOOSE_PHOTO);
                }
            }
        });
        addListener();
    }

    private void addListener() {
        mDynamicPublishTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {

            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    //添加用于添加图片的图片按钮
    private void addImg() {
        mList = new ArrayList<>();
        selectPhotos = new ArrayList<>();
        mList.add(R.mipmap.ic_launcher);
    }

    //显示PopupWindow
    private void showPopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopupWindow();
        }
    }

    //PopupWindow界面初始化及及相关操作
    private void initPopupWindow() {
        final View view = getLayoutInflater().inflate(R.layout.dynamic_publish_popup_window, null);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        //点击其他地方消失
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (view != null && view.isShown()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
                return false;
            }
        });
        Button cameraButton = (Button) view.findViewById(R.id.dynamic_publish_popupWindow_camera);
        Button pictureButton = (Button) view.findViewById(R.id.dynamic_publish_popupWindow_picture);
        //调用相机拍照
        //这里有bug
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(Environment.getExternalStorageDirectory(),
                        "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                path = outputImage.getPath();
                mUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                //启动相机程序
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        //调用系统相册
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPickerIntent photoPickerIntent = new PhotoPickerIntent
                        (PublishDynamicActivity.this);
                photoPickerIntent.setPhotoCount(9);
                photoPickerIntent.setColumn(3);
                startActivityForResult(photoPickerIntent, CHOOSE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //有bug
                    mList.clear();
                    mList.add(path);
                    mList.add(R.mipmap.ic_launcher);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case CHOOSE_PHOTO:
                List<String> photos = null;
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPickerActivity
                                .KEY_SELECTED_PHOTOS);
                    }
                    selectPhotos.clear();
                    if (photos != null) {
                        selectPhotos.addAll(photos);
                    }
                    mList.clear();
                    mList.addAll(selectPhotos);
                    mList.add(R.mipmap.ic_launcher);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
}
