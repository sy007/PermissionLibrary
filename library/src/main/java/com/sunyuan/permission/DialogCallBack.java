package com.sunyuan.permission;

import android.app.Dialog;
import android.content.Context;

import java.util.Set;

/**
 * author:Six
 * Date:2019/6/12
 */
public interface DialogCallBack {
    /**
     * 创建权限被拒后弹窗提示
     *
     * @param context     activity
     * @param tipInfo     外部设置的弹窗信息{@link PermissionFeature#setTip(TipInfo)}
     *                    当外部没设置时为null
     * @param permissions 被拒绝的权限
     * @param handle      告诉框架处理结果
     *                    {@link PremissionHandle#proceed()} 弹窗后点击确定告诉框架处理
     *                    {@link PremissionHandle#cancel()} 弹窗后点击取消告诉框架取消处理
     * @return 权限被拒绝后提示dialog
     */
    Dialog createDialog(Context context, TipInfo tipInfo, Set<String> permissions, PremissionHandle handle);
}
