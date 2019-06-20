package com.sunyuan.permission;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Set;

/**
 * author:Six
 * Date:2019/6/11
 */
public interface PermissionFeature {

    /**
     * 设置权限同意或拒绝后的回调
     *
     * @param requestPermissionListener
     * @return
     */
    @NonNull
    PermissionFeature addRequestPermissionListener(RequestPermissionListener requestPermissionListener);

    /**
     * 权限拒绝后是否显示弹窗提示 会覆盖{@link PermissionConfig}中配置的{@code showTip}
     *
     * @param isShowTip 是否显示弹窗提示 默认显示
     * @return
     */
    @NonNull
    PermissionFeature showTip(boolean isShowTip);

    /**
     * 设置弹窗信息{@link TipInfo}
     * 设置的{@code tipInfo}数据将被注入到
     * {@link DialogCallBack#createDialog(Context, TipInfo, Set, PremissionHandle)
     *
     * @param tipInfo
     * @return
     */
    @NonNull
    PermissionFeature setTip(TipInfo tipInfo);

    /**
     * 请求权限
     *
     * @param requestCode 用于区分请求不同权限触发时机
     */
    void request(int requestCode);
}
