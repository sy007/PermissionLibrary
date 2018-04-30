package com.sunyuan.permissionsimple;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sunyuan.permissionsimple.permission.PermissionFail;
import com.sunyuan.permissionsimple.permission.PermissionSuccess;
import com.sunyuan.permissionsimple.permission.PermissionsUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CAMERA_REQUEST_CODE = 0;
    private ImageView ivCameraResult;
    public static final String TAG = MainActivity.class.getSimpleName();
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivCameraResult = findViewById(R.id.iv_camera_result);
        findViewById(R.id.btn_camera_request).setOnClickListener(this);
        findViewById(R.id.btn_callphone_request).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_camera_request:
                PermissionsUtil.requestPermission(this,
                        201,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.btn_callphone_request:
                PermissionsUtil.requestPermission(this,
                        200,
                        Manifest.permission.CALL_PHONE);
                break;
            default:
                break;
        }
    }

    @PermissionSuccess(requestCode = 201)
    public void launchCameraSucc() {
        Toast.makeText(this, "相机权限请求成功", Toast.LENGTH_SHORT).show();
        launchCamera();
    }

    @PermissionFail(requestCode = 201)
    public void launchCameraError() {
        Toast.makeText(this, "相机权限请求失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionSuccess(requestCode = 200)
    public void callPhoneSucc() {
        Toast.makeText(this, "拨打电话权限请求成功", Toast.LENGTH_SHORT).show();
        diallPhone("123456");
    }

    @PermissionFail(requestCode = 200)
    public void callPhoneError() {
        Toast.makeText(this, "拨打电话权限请求失败", Toast.LENGTH_SHORT).show();
    }

    public void diallPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getCacheDir().getAbsolutePath() +
                "/sunyuan/" +
                System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Log.d(TAG, "camear result uri:" + FileProvider.getUriForFile(this, getPackageName() + ".provider", file));
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.d(TAG, "camera reult bitmap size:" + bitmap.getByteCount());
                    ivCameraResult.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    break;
                default:
                    break;
            }
        }
    }
}
