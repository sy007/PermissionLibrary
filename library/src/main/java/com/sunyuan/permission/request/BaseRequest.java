package com.sunyuan.permission.request;

import android.content.Context;

import com.sunyuan.permission.PermissionFeature;
import com.sunyuan.permission.PermissionsUtil;
import com.sunyuan.permission.RequestPermissionListener;
import com.sunyuan.permission.TipInfo;

/**
 * author:Six
 * Date:2019/6/20
 */
public abstract class BaseRequest implements PermissionFeature {

    protected Context context;
    protected RequestPermissionListener requestPermissionListener;
    protected TipInfo tipInfo;
    protected boolean isShowTip = PermissionsUtil.permissionConfig == null ||
            PermissionsUtil.permissionConfig.isShowTip();

    public BaseRequest(Context context) {
        this.context = context;
    }

    @Override
    public PermissionFeature addRequestPermissionListener(RequestPermissionListener requestPermissionListener) {
        this.requestPermissionListener = requestPermissionListener;
        return this;
    }

    @Override
    public PermissionFeature  showTip(boolean isShowTip) {
        this.isShowTip = isShowTip;
        return  this;
    }

    @Override
    public PermissionFeature setTip(TipInfo tipInfo) {
        this.tipInfo = tipInfo;
        return  this;
    }

}
