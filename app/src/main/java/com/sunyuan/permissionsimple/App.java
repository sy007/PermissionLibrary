package com.sunyuan.permissionsimple;

import android.app.Application;

import com.sunyuan.permission.PermissionConfig;
import com.sunyuan.permission.PermissionsUtil;
import com.sunyuan.permission.TipInfo;

/**
 * author:Six
 * Date:2019/6/12
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TipInfo tipInfo = new TipInfo.Builder().setCancel("取消")
                .setEnsure("设置")
                .setTitle("温馨提示")
                .setContent("请到权限管理打开权限")
                .build();
        PermissionsUtil.init(new PermissionConfig.Builder().setTipInfo(tipInfo).showTip(true)
                .build());
    }
}
