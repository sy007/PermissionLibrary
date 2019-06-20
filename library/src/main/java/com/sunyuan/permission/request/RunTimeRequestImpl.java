package com.sunyuan.permission.request;

import android.content.Context;

import com.sunyuan.permission.PermissionActivity;

import java.util.Objects;

/**
 * author:Six
 * Date:2019/6/20
 */
public class RunTimeRequestImpl extends BaseRequest implements RunTimeRequest {

    private String[] permissions;

    public RunTimeRequestImpl(Context context) {
        super(context);
    }

    public RunTimeRequest needRequestPermissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }


    @Override
    public void request(int requestCode) {
        Objects.requireNonNull(permissions, "permissions connot be empty");
        PermissionActivity.startRunTimeActivity(context,
                requestCode,
                permissions,
                isShowTip,
                tipInfo, requestPermissionListener);
    }
}
