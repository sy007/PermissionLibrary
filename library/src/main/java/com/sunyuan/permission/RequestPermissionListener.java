package com.sunyuan.permission;

/**
 * author: Six
 * Created by on 2018/8/17
 */
public interface RequestPermissionListener {

    void onRequestSuccess(int requestCode);

    void onRequestFail(int requestCode);
}
