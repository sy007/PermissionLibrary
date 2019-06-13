package com.sunyuan.permission;

import android.content.Context;

import java.util.Objects;

/**
 * author:Six
 * Date:2019/6/13
 * <p>
 * 暂未实现
 */
public class PermissionApplication implements PermissionFeature {
    private Context context;
    private String[] permissions;
    private RequestPermissionListener requestPermissionListener;
    private boolean showTip;
    private TipInfo tipInfo;

    public PermissionApplication(Context context) {
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
        return null;
    }

    @Override
    public PermissionFeature showTip(boolean showTip) {
        this.showTip = showTip;
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
    }
}
