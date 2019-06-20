package com.sunyuan.permissionsimple;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * author：six
 * created by:2019-06-16
 * github:https://github.com/sy007
 */
public class Utils {

    /**
     * 根据危险权限的字符串值获取它的中文名称
     *
     * @param permission 危险权限的字符串值
     * @return 权限中文名称
     */
    public static String getPermissionName(String permission) {
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
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                permissionName = "联系人";
                break;
            case Manifest.permission.REQUEST_INSTALL_PACKAGES:
                permissionName = "安装";
                break;
            default:
                break;
        }
        return permissionName;
    }

}
