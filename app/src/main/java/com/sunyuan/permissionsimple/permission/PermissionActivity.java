package com.sunyuan.permissionsimple.permission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created  2018/4/30.
 *
 * @author six
 */
public class PermissionActivity extends AppCompatActivity {
    private int requestCode;
    private boolean isRequireCheck;
    private String[] permissions;
    private boolean showTip;jie
    private PermissionsUtil.TipInfo tipInfo;
    private final String defaultTitle = "帮助";
    private final String defaultContent = "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
    private final String defaultCancel = "取消";
    private final String defaultEnsure = "设置";
    private static RequestPermissionListener requestListener;
    private static final String KEY_PERMISSIONS = "KEY_PERMISSIONS";
    private static final String KEY_REQUEST_CODE = "KEY_REQUEST_CODE";
    private static final String KEY_TIP = "KEY_TIP";
    private static final String KEY_SHOW_TIP = "KEY_SHOW_TIP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(KEY_PERMISSIONS)) {
            finish();
            return;
        }
        isRequireCheck = true;
        Intent intent = getIntent();
        permissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
        requestCode = intent.getIntExtra(KEY_REQUEST_CODE, 0);
        showTip = intent.getBooleanExtra(KEY_SHOW_TIP, true);
        Serializable ser = intent.getSerializableExtra(KEY_TIP);
        if (ser == null) {
            tipInfo = new PermissionsUtil.TipInfo(defaultTitle, defaultContent, defaultCancel, defaultEnsure);
        } else {
            tipInfo = (PermissionsUtil.TipInfo) ser;
        }
    }

    public static void requestPermission(Context context, int requestCode, PermissionsUtil.TipInfo tipInfo, boolean showTip,
                                         String[] permissions, RequestPermissionListener requestListener) {
        PermissionActivity.requestListener = requestListener;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_REQUEST_CODE, requestCode);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        intent.putExtra(KEY_TIP, tipInfo);
        intent.putExtra(KEY_SHOW_TIP, showTip);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        // 请求权限,回调时会触发onResume
        super.onResume();
        if (isRequireCheck) {
            //判断授权是否都通过了
            if (PermissionsUtil.hasPermission(this, permissions)) {
                //如果授权都通过了
                permissionsGranted();
            } else {
                //申请权限的时只申请未授权过的权限
                requestPermissions(PermissionsUtil.getUnGrantedPermissions(PermissionActivity.this, permissions));
                isRequireCheck = false;
            }
        } else {
            isRequireCheck = true;
        }
    }

    /**
     * 请求权限兼容低版本
     *
     * @param permission
     */
    private void requestPermissions(String[] permission) {
        ActivityCompat.requestPermissions(this, permission, requestCode);
    }


    /**
     * 用户权限处理, * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断

        if (requestCode == PermissionActivity.this.requestCode
                && PermissionsUtil.isGranted(grantResults)
                && PermissionsUtil.hasPermission(this, permissions)) {
            permissionsGranted();
        } else if (showTip) {
            String content = "当前应用缺少%s权限。\r\n请点击 \"设置\"-\"权限\"-打开所需权限。";
            Set<String> tempPermissionNames = new HashSet<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    String permissionName = getPermissionName(permissions[i]);
                    tempPermissionNames.add(permissionName);
                }
            }

            int tempPermissionNameSize = tempPermissionNames.size();
            StringBuilder sb = new StringBuilder();
            for (String temPermissionName : tempPermissionNames) {
                tempPermissionNameSize--;
                if (0 == tempPermissionNameSize) {
                    sb.append(temPermissionName);
                } else {
                    sb.append(temPermissionName)
                            .append(",");
                }
            }
            tipInfo.content = String.format(content, sb.toString());
            showMissingPermissionDialog();
        } else {
            //不需要提示用户
            permissionsDenied();
        }
    }


    /**
     * 根据危险权限的字符串值获取它的中文名称
     *
     * @param permission 危险权限的字符串值
     * @return 权限中文名称
     */

    private String getPermissionName(String permission) {
        String permissionName = "";
        switch (permission) {
            case Manifest.permission.CAMERA:
                permissionName = "相机";
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                permissionName = "存储";
                break;
            case Manifest.permission.RECORD_AUDIO:
                permissionName = "录音";
                break;
            case Manifest.permission.CALL_PHONE:
                permissionName = "拨打电话";
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                permissionName = "定位";
                break;
            default:
                break;
        }
        return permissionName;
    }


    /**
     * 显示缺失权限提示
     */

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle(TextUtils.isEmpty(tipInfo.title) ? defaultTitle : tipInfo.title);
        builder.setMessage(TextUtils.isEmpty(tipInfo.content) ? defaultContent : tipInfo.content);
        builder.setNegativeButton(TextUtils.isEmpty(tipInfo.cancel) ? defaultCancel : tipInfo.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                permissionsDenied();

            }
        });
        builder.setPositiveButton(TextUtils.isEmpty(tipInfo.ensure) ?
                defaultEnsure : tipInfo.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionsUtil.toSetting(PermissionActivity.this);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    private void permissionsDenied() {
        requestListener.onRequestFail();
        finish();
    }


    /**
     * 全部权限均已获取
     */
    private void permissionsGranted() {
        requestListener.onRequestSuccess();
        finish();
    }

    @Override
    public void finish() {
        requestListener = null;
        super.finish();
    }
}
