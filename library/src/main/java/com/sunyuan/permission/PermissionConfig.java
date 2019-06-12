package com.sunyuan.permission;

/**
 * author:Six
 * Date:2019/6/12
 */
public class PermissionConfig {
    private TipInfo tipInfo;

    private boolean showTip;

    public TipInfo getTipInfo() {
        return tipInfo;
    }

    public boolean isShowTip() {
        return showTip;
    }


    private PermissionConfig(Builder builder) {
        this.showTip = builder.showTip;
        this.tipInfo = builder.tipInfo;
    }

    public static class Builder {
        /**
         * 权限被拒绝后弹窗提醒文案配置
         * {@link TipInfo}
         */
        private TipInfo tipInfo;
        /**
         * 是否开启权限被拒绝后弹窗提醒
         * 默认为true
         */
        private boolean showTip = true;


        public Builder setTipInfo(TipInfo tipInfo) {
            this.tipInfo = tipInfo;
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
