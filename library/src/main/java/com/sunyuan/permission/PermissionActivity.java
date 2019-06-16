package com.sunyuan.permission;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author：six
 * created by:2019-06-16
 * github:https://github.com/sy007
 */
public class PermissionActivity extends AppCompatActivity implements PremissionHandle {
    private static final String TIP_INFO = "TIP_INFO";
    private static final String REQUEST_CODE = "REQUEST_CODE";
    private static final String PERMISSIONS = "PERMISSIONS";
    private static final String IS_SHOW_TIP = "IS_SHOW_TIP";
    private static RequestPermissionListener requestPermissionListener;
    private int requestCode;
    private boolean isShowTip;
    private String[] permissions;
    private TipInfo tipInfo;
    private boolean isProceed;


    /**
     * @param context
     * @param requestCode               请求权限时传入的code用于区分不同时机时的请求
     *                                  {@link PermissionFeature#request(int)}
     * @param permissions               需要请求哪些权限
     * @param isShowTip                 是否显示被拒绝时的弹窗提醒 默认为{@code true}
     * @param tipInfo                   修改弹窗提醒文案model{@link TipInfo}
     * @param requestPermissionListener 权限请求回调{@link RequestPermissionListener}
     */
    public static void startActivity(Context context, int requestCode, String[] permissions,
                                     boolean isShowTip, TipInfo tipInfo,
                                     RequestPermissionListener requestPermissionListener) {
        PermissionActivity.requestPermissionListener = requestPermissionListener;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TIP_INFO, tipInfo);
        intent.putExtra(PERMISSIONS, permissions);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(IS_SHOW_TIP, isShowTip);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        requestCode = intent.getIntExtra(REQUEST_CODE, 200);
        permissions = intent.getStringArrayExtra(PERMISSIONS);
        tipInfo = (TipInfo) intent.getSerializableExtra(TIP_INFO);
        isShowTip = intent.getBooleanExtra(IS_SHOW_TIP, true);
        List<String> unGrantedPermissions = PermissionsUtil.getUnGrantedPermissions(PermissionActivity.this,
                permissions);
        if (!unGrantedPermissions.isEmpty()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Set<String> rejectPermissions = new HashSet<>(unGrantedPermissions);
                handleRejectPermission(rejectPermissions);
            } else {
                //申请权限的时只申请未授权过的权限
                requestPermissions(unGrantedPermissions);
            }
        } else {
            permissionsGranted();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isProceed) {
            //判断授权是否都通过了
            if (PermissionsUtil.hasPermission(PermissionActivity.this, permissions)) {
                isProceed = false;
                //如果授权都通过了
                permissionsGranted();
            } else {
                isProceed = false;
                //申请权限的时只申请未授权过的权限
                requestPermissions(PermissionsUtil.getUnGrantedPermissions(PermissionActivity.this,
                        permissions));
            }
        }
    }


    /**
     * 请求权限兼容低版本
     *
     * @param permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(List<String> permissions) {
        requestPermissions(permissions.toArray(new String[]{}), requestCode);
    }


    private void permissionsGranted() {
        if (requestPermissionListener != null) {
            requestPermissionListener.onRequestSuccess(requestCode);
        }
        finish();
    }


    private void permissionsDenied() {
        if (requestPermissionListener != null) {
            requestPermissionListener.onRequestFail(requestCode);
        }
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断
        if (this.requestCode == requestCode
                && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(PermissionActivity.this, permissions)) {
            permissionsGranted();
            return;
        }
        List<String> unGrantedPermissions = PermissionsUtil.getUnGrantedPermissions(PermissionActivity.this,
                permissions);
        Set<String> rejectPermissions = new HashSet<>(unGrantedPermissions);
        handleRejectPermission(rejectPermissions);
    }


    private void handleRejectPermission(Set<String> rejectPermissions) {
        if (isShowTip) {
            boolean isUseGlobalDialog = PermissionsUtil.permissionConfig != null &&
                    PermissionsUtil.permissionConfig.getDialogCallBack() != null;
            if (isUseGlobalDialog) {
                Dialog dialog = PermissionsUtil.permissionConfig.getDialogCallBack().
                        createDialog(PermissionActivity.this, tipInfo, rejectPermissions, this);
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
        permissionsDenied();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestPermissionListener = null;
    }
}
