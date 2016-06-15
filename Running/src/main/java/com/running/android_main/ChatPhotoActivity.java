package com.running.android_main;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.rong.imkit.tools.PhotoFragment;

/**
 * 显示聊天图片 EzioL
 */
public class ChatPhotoActivity extends AppCompatActivity {
    PhotoFragment mPhotoFragment;
    Uri mUri;
    Uri mDownloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatphoto);
        mPhotoFragment = (PhotoFragment)getSupportFragmentManager().findFragmentById(R.id.chat_photo_fragment);
        Uri uri = getIntent().getParcelableExtra("photo");
        Uri thumbUri = getIntent().getParcelableExtra("thumbnail");
        mUri = uri;
        if (uri != null)
            mPhotoFragment.initPhoto(uri, thumbUri, new PhotoFragment.PhotoDownloadListener() {
                @Override
                public void onDownloaded(Uri uri) {
                    mDownloaded = uri;
                }
                @Override
                public void onDownloadError() {
                }
            });

    }

    public void savePhoto(View view) {
        if(mDownloaded == null) {
            Toast.makeText(this, "正在下载，请稍后保存！", Toast.LENGTH_SHORT).show();

        }

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path, "RongCloud/Image");
        if(!dir.exists())
            dir.mkdirs();

        File from = new File(mDownloaded.getPath());
        String name = from.getName() + ".jpg";
        File to = new File(dir.getAbsolutePath(), name);
        if(to.exists()) {
            //Toast.makeText(this, "文件保存成功 "+to.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.e("Ezio123", "savePhoto: "+to.getAbsolutePath() );
        }
        copyFile(from.getAbsolutePath(), to.getAbsolutePath());
    }
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "文件保存出错！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            Toast.makeText(this, "文件保存成功！", Toast.LENGTH_SHORT).show();
        }
    }

}
