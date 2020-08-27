package com.sunyuan.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
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
    private static final int INSTALL_REQUEST = 100;
    private static final int RUN_TIME_REQUEST = 200;
    private static final int INSTALL_PERMISS_CODE = 300;
    private static final String REQUEST_TYPE = "REQUEST_TYPE";
    private static RequestPermissionListener requestPermissionListener;
    private int requestCode;
    private boolean isShowTip;
    private String[] permissions;
    private TipInfo tipInfo;
    private boolean isProceed;
    private int requestType;

    /**
     * @param context
     * @param requestCode               请求权限时传入的code用于区分不同时机时的请求
     *                                  {@link PermissionFeature#request(int)}
     * @param permissions               需要请求哪些权限
     * @param isShowTip                 是否显示被拒绝时的弹窗提醒 默认为{@code true}
     * @param tipInfo                   修改弹窗提醒文案model{@link TipInfo}
     * @param requestPermissionListener 权限请求回调{@link RequestPermissionListener}
     */
    public static void startRunTimeActivity(Context context, int requestCode, String[] permissions,
                                            boolean isShowTip, TipInfo tipInfo,
                                            RequestPermissionListener requestPermissionListener) {
        PermissionActivity.requestPermissionListener = requestPermissionListener;
        Intent intent = buildIntent(context, requestCode, isShowTip, tipInfo);
        intent.putExtra(PERMISSIONS, permissions);
        intent.putExtra(REQUEST_TYPE, RUN_TIME_REQUEST);
        context.startActivity(intent);
    }


    @NonNull
    private static Intent buildIntent(Context context, int requestCode, boolean isShowTip, TipInfo tipInfo) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TIP_INFO, tipInfo);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(IS_SHOW_TIP, isShowTip);
        return intent;
    }


    /**
     * @param context
     * @param requestCode
     * @param isShowTip
     * @param tipInfo
     * @param requestPermissionListener
     */
    public static void startInstallActivity(Context context, int requestCode,
                                            boolean isShowTip, TipInfo tipInfo,
                                            RequestPermissionListener requestPermissionListener) {
        PermissionActivity.requestPermissionListener = requestPermissionListener;
        Intent intent = buildIntent(context, requestCode, isShowTip, tipInfo);
        intent.putExtra(REQUEST_TYPE, INSTALL_REQUEST);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        requestCode = intent.getIntExtra(REQUEST_CODE, 200);
        tipInfo = (TipInfo) intent.getSerializableExtra(TIP_INFO);
        isShowTip = intent.getBooleanExtra(IS_SHOW_TIP, true);


        requestType = intent.getIntExtra(REQUEST_TYPE, RUN_TIME_REQUEST);
        switch (requestType) {
            case RUN_TIME_REQUEST:
                handleRunTimePermission(intent);
                break;
            case INSTALL_REQUEST:
                handleInstallPermission();
                break;
        }
    }

    private void handleRunTimePermission(Intent intent) {
        permissions = intent.getStringArrayExtra(PERMISSIONS);
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


    private void handleInstallPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean installAllowed = getPackageManager().canRequestPackageInstalls();
            if (installAllowed) {
                permissionsGranted();
            } else {
                requestPermissions(Arrays.asList(Manifest.permission.REQUEST_INSTALL_PACKAGES));
            }
        } else {
            permissionsGranted();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        switch (requestType) {
            case RUN_TIME_REQUEST:
                handleRunTimePermissionHintAfter();
                break;
            case INSTALL_REQUEST:
                handleInstallPermissionHintAfter();
                break;
        }
    }

    private void handleInstallPermissionHintAfter() {
        if (isProceed) {
            handleInstallPermission();
            isProceed = false;
        }
    }

    private void handleRunTimePermissionHintAfter() {
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


    private void permissionsDenied(Set<String> permissions) {
        if (requestPermissionListener != null) {
            requestPermissionListener.onRequestFail(requestCode, permissions);
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
            permissionsDenied(rejectPermissions);
        } else {
            //不需要提示用户
            permissionsDenied(rejectPermissions);
        }
    }

    @Override
    public void proceed() {
        isProceed = true;
        toSetting();
    }


    public void toSetting() {
        switch (requestType) {
            case RUN_TIME_REQUEST:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                break;
            case INSTALL_REQUEST:
                intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, INSTALL_PERMISS_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestType) {
            case INSTALL_REQUEST:
                if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISS_CODE) {
                    permissionsGranted();
                }
                break;
        }
    }

    @Override
    public void cancel() {
        permissionsDenied(new HashSet<String>());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestPermissionListener = null;
    }
}
