package com.sunyuan.permissionsimple;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.sunyuan.permission.DialogCallBack;
import com.sunyuan.permission.PermissionConfig;
import com.sunyuan.permission.PermissionsUtil;
import com.sunyuan.permission.PremissionHandle;
import com.sunyuan.permission.TipInfo;

import java.util.Set;

/**
 * author:Six
 * Date:2019/6/12
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PermissionConfig permissionConfig = new PermissionConfig.Builder().
                showTip(true)
                .setDialogCallBack(createDialog())
                .build();
        PermissionsUtil.init(permissionConfig);
    }

    /**
     * 创建权限被拒后的全局统一风格弹窗
     *
     * @return
     */
    private DialogCallBack createDialog() {
        return new DialogCallBack() {
            @Override
            public Dialog createDialog(final Context context, TipInfo tipInfo, Set<String> permissions, final PremissionHandle handle) {
                //当外部没设置弹窗信息时为null,这时我们要自己处理
                if (tipInfo == null) {
                    String content = "当前应用缺少%s权限。\r\n请点击 \"设置\"-\"权限管理\"-打开所需权限。";
                    StringBuilder sb = new StringBuilder();
                    int permissionNameSize = permissions.size();
                    for (String p : permissions) {
                        permissionNameSize--;
                        if (0 == permissionNameSize) {
                            sb.append(getPermissionName(p));
                        } else {
                            sb.append(getPermissionName(p))
                                    .append(",");
                        }
                    }
                    tipInfo = new TipInfo.Builder().setTitle("温馨提示")
                            .setCancel("取消")
                            .setContent(String.format(content, sb.toString()))
                            .setEnsure("设置").build();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(TextUtils.isEmpty(tipInfo.getTitle()) ? "" : tipInfo.getTitle());
                builder.setMessage(TextUtils.isEmpty(tipInfo.getContent()) ? "" : tipInfo.getContent());
                builder.setNegativeButton(TextUtils.isEmpty(tipInfo.getCancel()) ? "" : tipInfo.getCancel(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //告诉框架取消处理
                                handle.cancel();
                            }
                        });
                builder.setPositiveButton(TextUtils.isEmpty(tipInfo.getEnsure()) ? "" : tipInfo.getEnsure(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //告诉框架处理
                                handle.proceed();
                                toSetting(context);
                            }
                        });
                builder.setCancelable(false);
                return builder.create();
            }
        };
    }


    /**
     * 根据危险权限的字符串值获取它的中文名称
     *
     * @param permission 危险权限的字符串值
     * @return 权限中文名称
     */
    static String getPermissionName(String permission) {
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
            default:
                break;
        }
        return permissionName;
    }

    static void toSetting(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
