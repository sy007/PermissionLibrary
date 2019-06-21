package com.sunyuan.permission.operate;

import com.sunyuan.permission.request.InstallRequest;
import com.sunyuan.permission.request.RunTimeRequest;

/**
 * author:Six
 * Date:2019/6/20
 */
public interface Operate {

    RunTimeRequest runTime();

    InstallRequest install();
}

