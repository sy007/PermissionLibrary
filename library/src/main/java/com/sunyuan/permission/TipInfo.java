package com.sunyuan.permission;

import java.io.Serializable;

/**
 * author:Six
 * Date:2019/6/11
 */
public class TipInfo implements Serializable {

    /**
     * 标题
     */
    String title;
    /**
     * 内容
     */
    String content;
    /**
     * 取消按钮文本
     */
    String cancel;
    /**
     * 确定按钮文本
     */
    String ensure;


    public String getTitle() {
        return title;
    }


    public String getContent() {
        return content;
    }

    public String getCancel() {
        return cancel;
    }



    public String getEnsure() {
        return ensure;
    }



    public TipInfo(Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
        this.cancel = builder.cancel;
        this.ensure = builder.ensure;
    }


    public static class Builder {
        private String title;
        private String content;
        private String cancel;
        private String ensure;


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        public Builder setContent(String content) {
            this.content = content;
            return this;
        }


        public Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder setEnsure(String ensure) {
            this.ensure = ensure;
            return this;
        }

        public TipInfo build() {
            return new TipInfo(this);
        }
    }

}
