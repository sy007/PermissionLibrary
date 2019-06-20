package com.sunyuan.permission.request;

import com.sunyuan.permission.PermissionFeature;

/**
 * author:Six
 * Date:2019/6/20
 */
public interface RunTimeRequest extends PermissionFeature {
    RunTimeRequest needRequestPermissions(String... permissions);
}
