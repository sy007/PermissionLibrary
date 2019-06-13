package com.sunyuan.permission;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created  2018/4/30.
 *
 * @author six
 */

public class PermissionsUtil {

    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    static PermissionConfig permissionConfig;


    public static void init(PermissionConfig permissionConfig) {
        PermissionsUtil.permissionConfig = permissionConfig;
    }


    public static PermissionFeature with(Fragment fragment) {
        return getSupportFragment(fragment.getChildFragmentManager());
    }

    public static PermissionFeature with(FragmentActivity fragmentActivity) {
        return getSupportFragment(fragmentActivity.getSupportFragmentManager());
    }

    @Nullable
    public static PermissionFeature with(Context context) {
        if (!(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return getSupportFragment(((FragmentActivity) context).getSupportFragmentManager());
            } else if (context instanceof Activity) {
                return getSupportFragment(((Activity) context).getFragmentManager());
            } else if (context instanceof ContextWrapper) {
                return with(((ContextWrapper) context).getBaseContext());
            }
        }
        //暂未实现application中申请权限
        return null;
    }


    private static PermissionFeature getSupportFragment(android.app.FragmentManager fragmentManager) {
        PermissionFragment current = (PermissionFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = new PermissionFragment();
            fragmentManager.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return current;
    }

    private static PermissionFeature getSupportFragment(FragmentManager supportFragmentManager) {
        PermissionSupportFragment current = (PermissionSupportFragment) supportFragmentManager.findFragmentByTag(
                FRAGMENT_TAG);
        if (current == null) {
            current = new PermissionSupportFragment();
            supportFragmentManager.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            supportFragmentManager.executePendingTransactions();
        }
        return current;
    }


    /**
     * 判断权限是否授权:只要有一个没有授权就返回false
     *
     * @param context
     * @param permissions
     * @return
     */
    static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
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
    static List<String> getUnGrantedPermissions(Context context, String... permissions) {
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
