package com.sunyuan.permission;

/**
 * author:Six
 * Date:2019/6/12
 */
public class PermissionConfig {
    private boolean showTip;
    private DialogCallBack dialogCallBack;


    public boolean isShowTip() {
        return showTip;
    }

    public DialogCallBack getDialogCallBack() {
        return dialogCallBack;
    }


    private PermissionConfig(Builder builder) {
        this.showTip = builder.showTip;
        this.dialogCallBack = builder.dialogCallBack;
    }

    public static class Builder {

        /**
         * 是否开启权限被拒绝后弹窗提醒
         * 默认为true
         */
        private boolean showTip = true;

        /**
         * 设置统一弹窗
         */
        private DialogCallBack dialogCallBack;

        public Builder setDialogCallBack(DialogCallBack dialogCallBack) {
            this.dialogCallBack = dialogCallBack;
            return this;
        }


        public Builder showTip(boolean showTip) {
            this.showTip = showTip;
            return this;
        }

        public PermissionConfig build() {
            return new PermissionConfig(this);
        }
    }
}
