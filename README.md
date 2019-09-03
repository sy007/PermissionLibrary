# PermissionLibrary
一个Android权限封装库。
- [x] 支持Fragment,四大组件,application中使用
- [x] 支持单个或多个权限请求配置
- [x] 支持全局弹窗提示配置
# 使用
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
    
    
implementation 'com.github.sy007:PermissionLibrary:1.1.5-release'
```
在**Application**中全局配置
```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PermissionConfig permissionConfig = new PermissionConfig.Builder().
                showTip(true)
                .setDialogCallBack(createDialog())
                .build();
        PermissionsUtil.init(permissionConfig);
    }

    /**
     * 创建权限被拒后的全局统一风格弹窗
     *
     * @return
     */
    private DialogCallBack createDialog() {
        return new DialogCallBack() {
            @Override
            public Dialog createDialog(final Context context, TipInfo tipInfo, Set<String> permissions, final PremissionHandle handle) {
                //当外部没设置弹窗信息时为null,这时我们要自己处理
                if (tipInfo == null) {
                    String content = "当前应用缺少%s权限。\r\n请点击 \"设置\"-\"权限管理\"-打开所需权限。";
                    StringBuilder sb = new StringBuilder();
                    int permissionNameSize = permissions.size();
                    for (String p : permissions) {
                        permissionNameSize--;
                        if (0 == permissionNameSize) {
                            sb.append(getPermissionName(p));
                        } else {
                            sb.append(getPermissionName(p))
                                    .append(",");
                        }
                    }
                    tipInfo = new TipInfo.Builder().setTitle("温馨提示")
                            .setCancel("取消")
                            .setContent(String.format(content, sb.toString()))
                            .setEnsure("设置").build();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(TextUtils.isEmpty(tipInfo.getTitle()) ? "" : tipInfo.getTitle());
                builder.setMessage(TextUtils.isEmpty(tipInfo.getContent()) ? "" : tipInfo.getContent());
                builder.setNegativeButton(TextUtils.isEmpty(tipInfo.getCancel()) ? "" : tipInfo.getCancel(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //告诉框架取消处理
                                handle.cancel();
                            }
                        });
                builder.setPositiveButton(TextUtils.isEmpty(tipInfo.getEnsure()) ? "" : tipInfo.getEnsure(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //告诉框架处理
                                handle.proceed();
                                toSetting(context);
                            }
                        });
                builder.setCancelable(false);
                return builder.create();
            }
        };
    }

    static void toSetting(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
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
     * 权限拒绝后是否显示弹窗提示 会覆盖{@link PermissionConfig}中配置的showTip
     *
     * @param showTip 是否显示弹窗提示 默认显示
     * @return
     */
    PermissionFeature showTip(boolean showTip);

    /**
     * 设置弹窗信息{@link TipInfo}
     * 设置的{@code tipInfo}数据将被注入到{@link DialogCallBack#createDialog(Context, TipInfo, Set, PremissionHandle)
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
   
