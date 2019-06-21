package com.sunyuan.permission.operate;

import android.content.Context;

import com.sunyuan.permission.request.InstallRequest;
import com.sunyuan.permission.request.InstallRequestImpl;
import com.sunyuan.permission.request.RunTimeRequest;
import com.sunyuan.permission.request.RunTimeRequestImpl;

/**
 * author:Six
 * Date:2019/6/20
 */
public class OperateImpl implements Operate {
    private Context context;

    public OperateImpl(Context context) {
        this.context = context;
    }

    @Override
    public RunTimeRequest runTime() {
        return new RunTimeRequestImpl(context);
    }

    @Override
    public InstallRequest install() {
        return new InstallRequestImpl(context);
    }
}
