package com.sunyuan.permission;

/**
 * author:Six
 * Date:2019/6/11
 */
public interface PermissionFeature {

    /**
     * 需要请求的权限
     *
     * @param permissions
     * @return
     */
    PermissionFeature needRequestPermissions(String... permissions);

    /**
     * 设置权限同意或拒绝后的回调
     *
     * @param requestPermissionListener
     * @return
     */
    PermissionFeature addRequestPermissionListener(RequestPermissionListener requestPermissionListener);

    /**
     * 权限拒绝后是否显示弹窗提示
     *
     * @param showTip 是否显示弹窗提示 默认显示
     * @return
     */
    PermissionFeature showTip(boolean showTip);

    /**
     * 设置弹窗信息{@link TipInfo}
     *
     * @param tipInfo
     * @return
     */
    PermissionFeature setTip(TipInfo tipInfo);

    /**
     * 请求权限
     *
     * @param requestCode 用于区分请求不同权限触发时机
     */
    void request(int requestCode);
}
