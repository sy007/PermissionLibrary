package com.sunyuan.permission.operator;

import android.content.Context;

import com.sunyuan.permission.request.InstallRequest;
import com.sunyuan.permission.request.InstallRequestImpl;
import com.sunyuan.permission.request.RunTimeRequest;
import com.sunyuan.permission.request.RunTimeRequestImpl;

/**
 * author:Six
 * Date:2019/6/20
 */
public class OperatorImpl implements Operator {
    private Context context;

    public OperatorImpl(Context context) {
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
