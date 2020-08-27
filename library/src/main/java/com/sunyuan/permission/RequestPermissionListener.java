package com.sunyuan.permission;

import java.util.Set;

/**
 * author: Six
 * Created by on 2018/8/17
 */
public interface RequestPermissionListener {

    void onRequestSuccess(int requestCode);

    void onRequestFail(int requestCode, Set<String> permissions);
}
