package com.ai.cwf.unrar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ai.cwf.unrar.utils.FileUtils;
import com.ai.cwf.unrar.utils.ZipUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public int FILE_SELECT_CODE_UNZIP = 0x001;
    public int FILE_SELECT_CODE_ZIP = 0x003;
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == 0) {
                if (alertDialog != null && !alertDialog.isShowing()) {
                    alertDialog.show();
                }
            } else {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        }
    };

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.unzip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    FileUtils.showFileChooser(MainActivity.this, FILE_SELECT_CODE_UNZIP);
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_SELECT_CODE_UNZIP);
                }
            }
        });
        findViewById(R.id.zip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    FileUtils.showFileChooser(MainActivity.this, FILE_SELECT_CODE_ZIP);
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_SELECT_CODE_ZIP);
                }
            }
        });
        alertDialog = new AlertDialog.Builder(this).setMessage("正在操作，请稍等...").setCancelable(false).create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FILE_SELECT_CODE_UNZIP || requestCode == FILE_SELECT_CODE_ZIP) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    FileUtils.showFileChooser(MainActivity.this, requestCode);
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if ((requestCode == FILE_SELECT_CODE_UNZIP || requestCode == FILE_SELECT_CODE_ZIP) && resultCode == RESULT_OK) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(MainActivity.this, uri);
                    ToastUtils.show("路径：" + path);
                    String name = new File(path).getName();
                    name = name.substring(0, name.lastIndexOf("."));
                    try {
                        if (requestCode == FILE_SELECT_CODE_UNZIP) {
                            ToastUtils.show("解压文件目录：" + ZipUtils.UnZipFolder(path, new File(getExternalCacheDir(), name).getAbsolutePath()), true);
                        } else {
                            name += ".rar";
                            ToastUtils.show("压缩文件目录：" + ZipUtils.ZipFolder(path, new File(getExternalCacheDir(), name).getAbsolutePath()), true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show("发生错误：" + e.getMessage(), true);
                    }
                    handler.sendEmptyMessage(1);
                }
            }).start();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
