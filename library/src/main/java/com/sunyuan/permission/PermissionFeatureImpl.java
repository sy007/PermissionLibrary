package com.sunyuan.permission;

import android.content.Context;

import java.util.Objects;

/**
 * author：six
 * created by:2019-06-16
 * github:https://github.com/sy007
 */
public class PermissionFeatureImpl implements PermissionFeature {
    private Context context;
    private String[] permissions;
    private RequestPermissionListener requestPermissionListener;
    private TipInfo tipInfo;
    private boolean isShowTip = PermissionsUtil.permissionConfig == null ||
            PermissionsUtil.permissionConfig.isShowTip();

    public PermissionFeatureImpl(Context context) {
        this.context = context;
    }

    @Override
    public PermissionFeature needRequestPermissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    @Override
    public PermissionFeature addRequestPermissionListener(RequestPermissionListener requestPermissionListener) {
        this.requestPermissionListener = requestPermissionListener;
        return this;
    }

    @Override
    public PermissionFeature showTip(boolean isShowTip) {
        this.isShowTip = isShowTip;
        return this;
    }

    @Override
    public PermissionFeature setTip(TipInfo tipInfo) {
        this.tipInfo = tipInfo;
        return this;
    }

    @Override
    public void request(int requestCode) {
        Objects.requireNonNull(permissions, "permissions connot be empty");
        PermissionActivity.startActivity(context, requestCode, permissions, isShowTip,
                tipInfo, requestPermissionListener);
    }
}
