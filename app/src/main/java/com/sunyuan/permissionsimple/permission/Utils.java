package com.sunyuan.permissionsimple.permission;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created  2018/4/30.
 *
 * @author six
 */

public class Utils {
    public static boolean isEqualRequestCodeFromAnntation(Method m,
                                                          Class clazz,
                                                          int requestCode){
        if(clazz.equals(PermissionFail.class)){
            return requestCode == m.getAnnotation(PermissionFail.class).requestCode();
        } else if(clazz.equals(PermissionSuccess.class)){
            return requestCode == m.getAnnotation(PermissionSuccess.class).requestCode();
        } else {
            return false;
        }
    }

    public static <A extends Annotation> Method findMethodWithRequestCode(Class clazz,
                                                                          Class<A> annotation, int requestCode) {
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(annotation)){
                if(isEqualRequestCodeFromAnntation(method, annotation, requestCode)){
                    return method;
                }
            }
        }
        return null;
    }
}
