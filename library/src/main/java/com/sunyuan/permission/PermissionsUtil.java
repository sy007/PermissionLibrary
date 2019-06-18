package com.sunyuan.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created  2018/4/30.
 *
 * @author six
 */

public class PermissionsUtil {


    static PermissionConfig permissionConfig;


    public static void init(PermissionConfig permissionConfig) {
        PermissionsUtil.permissionConfig = permissionConfig;
    }


    @Nullable
    public static PermissionFeature with(Context context) {
        Objects.requireNonNull(context, "context connot be empty");
        return new PermissionFeatureImpl(context);
    }


    /**
     * 判断权限是否授权:只要有一个没有授权就返回false
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        if (permissions.length == 0) {
            return false;
        }
        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回未被授权的权限数组
     *
     * @param permissions 所有待检查是否授权的权限
     * @return
     */
    @NonNull
    public static List<String> getUnGrantedPermissions(Context context, String... permissions) {
        if (permissions.length == 0) {
            return new ArrayList<>();
        }
        List<String> permissionList = new ArrayList<>();
        //遍历权限数组，查找未被授权的权限
        for (String permission : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, permission);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        return permissionList;
    }

    /**
     * 判断一组授权结果是否为授权通过
     *
     * @param grantResult
     * @return
     */
    static boolean isGranted(@NonNull int... grantResult) {
        if (grantResult.length == 0) {
            return false;
        }
        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
