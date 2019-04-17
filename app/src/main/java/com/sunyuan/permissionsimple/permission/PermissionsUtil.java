package com.sunyuan.permissionsimple.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created  2018/4/30.
 *
 * @author six
 * <p>
 * 如何使用?参考下面写的一个例子
 * @see <a href="https://github.com/sy007/PermissionSimple">https://github.com/sy007/PermissionSimple</a>
 */

public class PermissionsUtil {
    public static final String TAG = PermissionsUtil.class.getSimpleName();


    /**
     * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
     *
     * @param activity
     * @param permission 要申请的权限
     */
    public static void requestPermission(Activity activity, int requestCode, String... permission) {
        requestPermission(activity, requestCode, permission, true, null);
    }

    /**
     * 申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
     *
     * @param activity
     * @param permission  需要申请授权的权限
     * @param requestCode
     * @param showTip     当用户拒绝授权时，是否显示提示
     * @param tip         当用户拒绝时要显示Dialog设置
     */
    public static void requestPermission(@NonNull final Activity activity,
                                         @NonNull final int requestCode,
                                         String[] permission,
                                         boolean showTip,
                                         @Nullable TipInfo tip) {
        String[] unGrantedPermissions = getUnGrantedPermissions(activity, permission);
        if (unGrantedPermissions != null && unGrantedPermissions.length > 0) {
            /**  该部分只有当系统是6.0以下的才会执行 */
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (PermissionsUtil.hasPermission(activity, permission)) {
                    executeSuccess(activity, requestCode);
                } else {
                    executeError(activity, requestCode);
                }
                return;
            }
            PermissionActivity.requestPermission(activity, requestCode, tip, showTip, unGrantedPermissions,
                    new RequestPermissionListener() {
                        @Override
                        public void onRequestSuccess() {
                            executeSuccess(activity, requestCode);
                        }

                        @Override
                        public void onRequestFail() {
                            executeError(activity, requestCode);
                        }
                    });
        } else {
            executeSuccess(activity, requestCode);
        }
    }

    public static void executeError(Activity activity, int requestCode) {
        Method executeMethod = Utils.findMethodWithRequestCode(activity.getClass(), PermissionFail.class, requestCode);
        methodInvoke(activity, executeMethod);
    }

    public static void executeSuccess(Activity activity, int requestCode) {
        Method executeMethod = Utils.findMethodWithRequestCode(activity.getClass(), PermissionSuccess.class, requestCode);
        methodInvoke(activity, executeMethod);
    }

    private static void methodInvoke(Activity activity, Method executeMethod) {
        if (executeMethod != null) {
            if (!executeMethod.isAccessible()) {
                executeMethod.setAccessible(true);
            }
            try {
                executeMethod.invoke(activity, new Object[]{});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断权限是否授权:只要有一个没有授权就返回false
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean hasPermission(@NonNull Activity activity, @NonNull String... permissions) {
        if (permissions.length == 0) {
            return false;
        }
        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(activity, per);
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
    public static String[] getUnGrantedPermissions(Activity activity, String... permissions) {

        if (permissions.length == 0) {
            return null;
        }
        String[] unGrantedPermissions;
        List<String> permissionList = new ArrayList<>();
        //遍历权限数组，查找未被授权的权限
        for (String permission : permissions) {
            int result = PermissionChecker.checkSelfPermission(activity, permission);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        //遍历List,给未赋值的权限列表赋值
        unGrantedPermissions = new String[permissionList.size()];
        for (int i = 0; i < permissionList.size(); i++) {
            unGrantedPermissions[i] = permissionList.get(i);
        }
        return unGrantedPermissions;
    }

    /**
     * 判断一组授权结果是否为授权通过
     *
     * @param grantResult
     * @return
     */
    public static boolean isGranted(@NonNull int... grantResult) {
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

    /**
     * 跳转到当前应用对应的设置页面
     */
    public static void toSetting(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }


    public static class TipInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        String title;

        String content;
        /**
         * 取消按钮文本
         */
        String cancel;
        /**
         * 确定按钮文本
         */
        String ensure;

        public TipInfo(@Nullable String title, @Nullable String content, @Nullable String cancel, @Nullable String ensure) {
            this.title = title;
            this.content = content;
            this.cancel = cancel;
            this.ensure = ensure;
        }
    }
}
