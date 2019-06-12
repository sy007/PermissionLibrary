package com.sunyuan.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.Set;

/**
 * author:Six
 * Date:2019/6/11
 */
public class PermissionFragment extends Fragment implements PermissionFeature, PremissionHandle {
    private Activity mActivity;
    private int requestCode;
    private String[] permissions;
    private boolean showTip = PermissionsUtil.permissionConfig == null ||
            PermissionsUtil.permissionConfig.isShowTip();
    private TipInfo tipInfo;
    private RequestPermissionListener requestPermissionListener;
    private boolean isProceed;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isProceed) {
            //判断授权是否都通过了
            if (PermissionsUtil.hasPermission(mActivity, permissions)) {
                isProceed = false;
                //如果授权都通过了
                permissionsGranted();
            } else {
                isProceed = false;
                //申请权限的时只申请未授权过的权限
                requestPermissions(PermissionsUtil.getUnGrantedPermissions(mActivity, permissions));
            }
        }
    }


    /**
     * 请求权限兼容低版本
     *
     * @param permission
     */
    private void requestPermissions(String[] permission) {
        requestPermissions(permission, requestCode);
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
        this.isProceed = false;
        this.requestCode = requestCode;
        /**  该部分只有当系统是6.0以下的才会执行 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (PermissionsUtil.hasPermission(mActivity, permissions)) {
                if (requestPermissionListener != null) {
                    requestPermissionListener.onRequestSuccess(requestCode);
                }
            } else {
                if (requestPermissionListener != null) {
                    requestPermissionListener.onRequestFail(requestCode);
                }
            }
            return;
        }
        String[] unGrantedPermissions = PermissionsUtil.getUnGrantedPermissions(mActivity, permissions);
        if (unGrantedPermissions != null && unGrantedPermissions.length > 0) {
            //申请权限的时只申请未授权过的权限
            requestPermissions(PermissionsUtil.getUnGrantedPermissions(mActivity, permissions));
        } else {
            if (requestPermissionListener != null) {
                requestPermissionListener.onRequestSuccess(requestCode);
            }
        }
    }


    private void permissionsGranted() {
        if (requestPermissionListener != null) {
            requestPermissionListener.onRequestSuccess(requestCode);
        }
    }


    private void permissionsDenied() {
        if (requestPermissionListener != null) {
            requestPermissionListener.onRequestFail(requestCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断
        if (this.requestCode == requestCode
                && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(mActivity, permissions)) {
            permissionsGranted();
            return;
        }
        Set<String> tempPermissionNames = new HashSet<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                String permissionName = PermissionsUtil.getPermissionName(permissions[i]);
                tempPermissionNames.add(permissionName);
            }
        }
        if (showTip) {
            boolean isUseGlobalDialog = PermissionsUtil.permissionConfig != null &&
                    PermissionsUtil.permissionConfig.getDialogCallBack() != null;
            if (isUseGlobalDialog) {
                Dialog dialog = PermissionsUtil.permissionConfig.getDialogCallBack().
                        createDialog(mActivity, tipInfo, tempPermissionNames, this);
                dialog.show();
                return;
            }
            permissionsDenied();
        } else {
            //不需要提示用户
            permissionsDenied();
        }
    }


    @Override
    public void proceed() {
        isProceed = true;
    }

    @Override
    public void cancel() {
        isProceed = false;
    }
}
