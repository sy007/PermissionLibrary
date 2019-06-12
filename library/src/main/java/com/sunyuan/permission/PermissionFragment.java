package com.sunyuan.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * author:Six
 * Date:2019/6/11
 */
public class PermissionFragment extends Fragment implements PermissionFeature {
    private Activity mActivity;
    private int requestCode;
    private String[] permissions;
    private boolean showTip = PermissionsUtil.permissionConfig == null ||
            PermissionsUtil.permissionConfig.isShowTip();
    private TipInfo tipInfo;
    private static final String DEFAUL_TTITLE = "帮助";
    private static final String DEFAULT_CANCEL = "取消";
    private static final String DEFAULT_ENSURE = "设置";
    private RequestPermissionListener requestPermissionListener;

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


    /**
     * 显示缺失权限提示
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(TextUtils.isEmpty(tipInfo.title) ? "" : tipInfo.title);
        builder.setMessage(TextUtils.isEmpty(tipInfo.content) ? "" : tipInfo.content);
        builder.setNegativeButton(TextUtils.isEmpty(tipInfo.cancel) ? "" : tipInfo.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(TextUtils.isEmpty(tipInfo.ensure) ?
                "" : tipInfo.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionsUtil.toSetting(mActivity);
            }
        });
        builder.setCancelable(false);
        builder.show();
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
        if (showTip) {
            if (tipInfo == null) {
                if (PermissionsUtil.permissionConfig != null &&
                        PermissionsUtil.permissionConfig.getTipInfo() != null) {
                    tipInfo = PermissionsUtil.permissionConfig.getTipInfo();
                } else {
                    String content = "当前应用缺少%s权限。\r\n请点击 \"设置\"-\"权限管理\"-打开所需权限。";
                    Set<String> tempPermissionNames = new HashSet<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            String permissionName = PermissionsUtil.getPermissionName(permissions[i]);
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
                    tipInfo = new TipInfo.Builder().setTitle(DEFAUL_TTITLE)
                            .setCancel(DEFAULT_CANCEL)
                            .setContent(String.format(content, sb.toString()))
                            .setEnsure(DEFAULT_ENSURE).build();
                }
            }
            showMissingPermissionDialog();
        } else {
            //不需要提示用户
            permissionsDenied();
        }
    }
}
