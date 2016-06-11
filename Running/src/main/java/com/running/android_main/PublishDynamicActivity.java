package com.running.android_main;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.running.adapters.DynamicPublishGridViewAdapter;
import com.running.beans.DynamicImgBean;
import com.running.myviews.MyGridView;
import com.running.myviews.TopBar;
import com.running.utils.GetQiNiuYunToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Call;

public class PublishDynamicActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
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

    GetQiNiuYunToken mGetQiNiuYunToken;
    String token;
    //七牛云外链域名
    String NET_PATH = "o8hzh2lfo.bkt.clouddn.com";
    //七牛云上传空间名
    String UPLOAD_SPACE_NAME = "running";
    //从数据库获得用户id
    int id = 1001;
    //imgName为上传到空间中的名称，为了确保唯一，用(id+系统当前时间)命名
    String imgName;

    String imgUrl;

    List<String> imgList = new ArrayList<String>();

    String url = "http://192.168.56.2:8080/RunningAppTest/dynamicOperateServlet";

    //定位
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //用户id
                    int dUId = 1;
                    String content = mDynamicPublishContent.getText().toString();
                    String location = mDynamicPublishLocation.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(new Date());
                    DynamicImgBean bean = new DynamicImgBean(dUId,imgList,content,time,location);
                    Gson gson = new Gson();
                    String dynamicBean = gson.toJson(bean);
                    OkHttpUtils.post()
                            .url(url)
                            .addParams("appRequest","AddDynamic")
                            .addParams("dynamicBean",dynamicBean)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e) {

                                }

                                @Override
                                public void onResponse(String response) {

                                }
                            });
                    break;
            }
        }
    };

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback =
            new GalleryFinal.OnHanlderResultCallback() {

                @Override
                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                    if (resultList != null) {
                        mList.clear();
                        mList.addAll(resultList);
                        mList.add(R.mipmap.ic_launcher);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onHanlderFailure(int requestCode, String errorMsg) {
                    Toast.makeText(PublishDynamicActivity.this, errorMsg, Toast.LENGTH_SHORT)
                            .show();
                }
            };

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
        mDynamicPublishPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size()==1) {
                    //只有文字没有图片
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what=1;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }else {
                    //有图片
                    for (int i = 0; i < mList.size() - 1; i++) {
                        PhotoInfo photoInfo = (PhotoInfo) mList.get(i);
                        upload(photoInfo.getPhotoPath());
                    }
                }
            }
        });

        mDynamicPublishLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locating();
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
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
            }
        });
        //选择多张图片相册
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, 9, mOnHanlderResultCallback);
            }
        });
    }

    /**
     * 上传图片到七牛云
     * @param path (图片本地路径)
     */
    public void upload(String path) {
        UploadManager uploadManager = new UploadManager();
        mGetQiNiuYunToken = new GetQiNiuYunToken();
        token = mGetQiNiuYunToken.getToken(UPLOAD_SPACE_NAME);
        imgName = id + "/" + System.currentTimeMillis() + ".jpg";
        uploadManager.put(path, imgName, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        //info.statusCode 回掉状态码
                        if (info.statusCode == 200) {
                            imgUrl = "http://" + NET_PATH + File.separator + imgName;
                            imgList.add(imgUrl);
                            Log.d("TAG", imgList.size() + "");
                            //如果图片上传完成，提示发布动态完成
                            if (imgList.size()==(mList.size()-1)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what=1;
                                        mHandler.sendMessage(message);
                                    }
                                }).start();
                            }
                            Toast.makeText(PublishDynamicActivity.this, "完成上传",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PublishDynamicActivity.this, info.statusCode + "上传失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
    }

    private void locating() {
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mBDLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                String position = bdLocation.getAddrStr();
                int k = position.indexOf("省");
                position=position.substring(k+1,position.length());
                mDynamicPublishLocation.setText(position);
            }
        };
        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mLocationClient = null;
    }
}
