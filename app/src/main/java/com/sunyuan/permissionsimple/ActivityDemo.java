package com.sunyuan.permissionsimple;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sunyuan.permission.PermissionsUtil;
import com.sunyuan.permission.RequestPermissionListener;

import java.io.File;

public class ActivityDemo extends Activity implements RequestPermissionListener, View.OnClickListener {

    public static final int CAMERA_REQUEST_CODE = 0;
    private ImageView ivCameraResult;
    public static final String TAG = FragmentActivityDemo.class.getSimpleName();
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_permission_request);
        ivCameraResult = findViewById(R.id.iv_camera_result);
        findViewById(R.id.btn_camera_request).setOnClickListener(this);
        findViewById(R.id.btn_callphone_request).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //打开相机拍照
            case R.id.btn_camera_request:
                PermissionsUtil.with(this)
                        .needRequestPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .addRequestPermissionListener(this)
                        .request(201);
                break;
            //拨打电话
            case R.id.btn_callphone_request:
                PermissionsUtil.with(this)
                        .needRequestPermissions(Manifest.permission.CALL_PHONE)
                        .addRequestPermissionListener(this)
                        .request(200);
                break;
            default:
                break;
        }
    }

    public void launchCameraSucc() {
        Toast.makeText(this, "相机权限请求成功", Toast.LENGTH_SHORT).show();
        launchCamera();
    }

    public void launchCameraError() {
        Toast.makeText(this, "相机权限请求失败", Toast.LENGTH_SHORT).show();
    }

    public void callPhoneSucc() {
        Toast.makeText(this, "拨打电话权限请求成功", Toast.LENGTH_SHORT).show();
        dialPhone("123456");
    }

    public void callPhoneError() {
        Toast.makeText(this, "拨打电话权限请求失败", Toast.LENGTH_SHORT).show();
    }

    public void dialPhone(String phoneNum) {
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

    @Override
    public void onRequestSuccess(int requestCode) {
        switch (requestCode) {
            case 200:
                callPhoneSucc();
                break;
            case 201:
                launchCameraSucc();
                break;
        }
    }

    @Override
    public void onRequestFail(int requestCode) {
        switch (requestCode) {
            case 200:
                callPhoneError();
                break;
            case 201:
                launchCameraError();
                break;
        }
    }


}
