package com.sunyuan.permission;

/**
 * author:Six
 * Date:2019/6/12
 */
public interface PremissionHandle {
    /**
     * 弹窗后点击确定告诉框架处理
     */
    void proceed();

    /**
     * 弹窗后点击取消告诉框架取消处理
     */
    void cancel();
}
