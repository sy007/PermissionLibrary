package com.sunyuan.permission.request;

import android.content.Context;

import com.sunyuan.permission.PermissionActivity;

/**
 * author:Six
 * Date:2019/6/20
 */
public class InstallRequestImpl extends BaseRequest implements InstallRequest {
    public InstallRequestImpl(Context context) {
        super(context);
    }

    @Override
    public void request(int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean installAllowed = context.getPackageManager().canRequestPackageInstalls();
            if (installAllowed) {
                requestPermissionListener.onRequestSuccess(requestCode);
            } else {
                PermissionActivity.startInstallActivity(context, requestCode, isShowTip, tipInfo, requestPermissionListener);
            }
        } else {
            requestPermissionListener.onRequestSuccess(requestCode);
        }

    }
}
