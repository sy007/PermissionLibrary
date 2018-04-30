# PermissionSimple
    一个Android权限封装例子，如需集成，复制permission文件夹下所有代码，记得在清单文件注册PermissionActivity还有相应的style
### 步骤一
```java
  PermissionsUtil.requestPermission(this,
                        200,
                        Manifest.permission.CALL_PHONE);
```
### 步骤二
```java
  @PermissionSuccess(requestCode = 200)
    public void callPhoneSucc() {
        Toast.makeText(this, "拨打电话权限请求成功", Toast.LENGTH_SHORT).show();
        diallPhone("123456");
    }

    @PermissionFail(requestCode = 200)
    public void callPhoneError() {
        Toast.makeText(this, "拨打电话权限请求失败", Toast.LENGTH_SHORT).show();
    }
```
   
