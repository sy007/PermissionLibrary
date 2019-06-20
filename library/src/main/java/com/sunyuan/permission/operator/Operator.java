package com.sunyuan.permission.operator;

import com.sunyuan.permission.request.InstallRequest;
import com.sunyuan.permission.request.RunTimeRequest;

/**
 * author:Six
 * Date:2019/6/20
 */
public interface Operator {

    RunTimeRequest runTime();

    InstallRequest install();
}

