package com.sunyuan.permission;

/**
 * author:Six
 * Date:2019/6/12
 */
public class PermissionConfig {
    private boolean isShowTip;
    private DialogCallBack dialogCallBack;


    public boolean isShowTip() {
        return isShowTip;
    }

    public DialogCallBack getDialogCallBack() {
        return dialogCallBack;
    }


    private PermissionConfig(Builder builder) {
        this.isShowTip = builder.isShowTip;
        this.dialogCallBack = builder.dialogCallBack;
    }

    public static class Builder {

        /**
         * 是否开启权限被拒绝后弹窗提醒
         * 默认为true
         */
        private boolean isShowTip = true;

        /**
         * 设置统一弹窗
         */
        private DialogCallBack dialogCallBack;

        public Builder setDialogCallBack(DialogCallBack dialogCallBack) {
            this.dialogCallBack = dialogCallBack;
            return this;
        }


        public Builder showTip(boolean isShowTip) {
            this.isShowTip = isShowTip;
            return this;
        }

        public PermissionConfig build() {
            return new PermissionConfig(this);
        }
    }
}
