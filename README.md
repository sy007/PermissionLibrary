# PermissionSimple
一个Android权限封装库。
- [x] 支持Activity和Fragment中使用
- [x] 支持单个或多个权限请求配置
- [x] 支持全局配置
# 使用
在**Application**中全局配置
```java
 public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TipInfo tipInfo = new TipInfo.Builder().setCancel("取消")
                .setEnsure("设置")
                .setTitle("温馨提示")
                .setContent("请到权限管理打开权限")
                .build();
        PermissionsUtil.init(new PermissionConfig.Builder().setTipInfo(tipInfo).showTip(true)
                .build());
    }
}
```
在**Activity**中使用
```java
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //打开相机拍照
            case R.id.btn_camera_request:
                PermissionsUtil.with(this)
                        .needRequestPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        //添加权限请求成功或拒绝回调通知        
                        .addRequestPermissionListener(this)
                        .request(201);
                break;
            //拨打电话
            case R.id.btn_callphone_request:
                PermissionsUtil.with(this)
                        .needRequestPermissions(Manifest.permission.CALL_PHONE)
                         //添加权限请求成功或拒绝回调通知   
                        .addRequestPermissionListener(this)
                        .request(200);
                break;
            default:
                break;
        }
    }
    
    //权限请求成功回调
    @Override
    public void onRequestSuccess(int requestCode) {
        switch (requestCode) {
            case 200:
                //拨打电话
                callPhoneSucc();
                break;
            case 201:
                //打开相机拍照
                launchCameraSucc();
                break;
        }
    }
    //权限请求失败回调
    @Override
    public void onRequestFail(int requestCode) {
        switch (requestCode) {
            case 200:
                //拨打电话
                callPhoneError();
                break;
            case 201:
                //打开相机拍照
                launchCameraError();
                break;
        }
    }
```
# 更多api调用
```java
    public interface PermissionFeature {

    /**
     * 需要请求的权限
     *
     * @param permissions
     * @return
     */
    PermissionFeature needRequestPermissions(String... permissions);

    /**
     * 设置权限同意或拒绝后的回调
     *
     * @param requestPermissionListener
     * @return
     */
    PermissionFeature addRequestPermissionListener(RequestPermissionListener requestPermissionListener);

    /**
     * 权限拒绝后是否显示弹窗提示
     *
     * @param showTip 是否显示弹窗提示 默认显示
     * @return
     */
    PermissionFeature showTip(boolean showTip);

    /**
     * 设置弹窗信息{@link TipInfo}
     *
     * @param tipInfo
     * @return
     */
    PermissionFeature setTip(TipInfo tipInfo);

    /**
     * 请求权限
     *
     * @param requestCode 用于区分请求不同权限触发时机
     */
    void request(int requestCode);
}
```
   
