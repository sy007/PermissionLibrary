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
        PermissionActivity.startInstallActivity(context, requestCode, isShowTip, tipInfo, requestPermissionListener);
    }
}
