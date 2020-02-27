package com.parryworld.rnappupdate;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.widget.Toast;
import android.os.Build;
import androidx.core.content.FileProvider;
import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.util.Log;
/**
 * Created by parryworld on 2016/11/18.
 */

public class RNAppUpdate extends ReactContextBaseJavaModule implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String versionName = "1.0.0";
    private int versionCode = 1;
    private String mAPKPath = "";
    public static final String ACTION_MANAGE_UNKNOWN_APP_SOURCES = "android.settings.MANAGE_UNKNOWN_APP_SOURCES";
 

    public RNAppUpdate(ReactApplicationContext reactContext) {
        super(reactContext);
        PackageInfo pInfo = null;
        try {
            pInfo = reactContext.getPackageManager().getPackageInfo(reactContext.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "RNAppUpdate";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("versionName", versionName);
        constants.put("versionCode", versionCode);
        return constants;
    }

    @ReactMethod
    public void installApk(String apkPath) {
        mAPKPath = apkPath;
        checkIsAndroidO();
    }

    public void checkIsAndroidO() {
        // android 8.0以上安装需要“应用内安装权限”
        if (Build.VERSION.SDK_INT >= 26) {
            // boolean b = getReactApplicationContext().getPackageManager().canRequestPackageInstalls();
            // if (b) {
            //     install();
            // } else {
            //     //请求应用内安装权限权限
            //     AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
            //                 // .setIcon(R.mipmap.ic_launcher)
            //     builder.setTitle("提示");
            //     builder.setMessage("升级新版本安装应用需要打开未知来源权限，请去设置中开启权限");
            //     builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            //         @Override
            //         public void onClick(DialogInterface dialogInterface, int i) {
            //             // 点击“是”前往权限开启
            //             requestPermission();
            //         }
            //     });
            //     builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            //         @Override
            //         public void onClick(DialogInterface dialogInterface, int i) {
            //             // 点击“否”关闭窗口
            //             dialogInterface.dismiss();
            //         }
            //     });
            //     AlertDialog alertDialog = builder.create();
            //     alertDialog.show();
            // }
            install();
        } else {
            install(); //安装apk
        }
    }

    public void requestPermission() {
        // 判断是否需要授权说明
        if (ActivityCompat.shouldShowRequestPermissionRationale(getCurrentActivity(), Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            Log.i("tttt", "shouldShowRequestPermissionRationale true");
            Toast.makeText(getReactApplicationContext(), "shouldShowRequestPermissionRationale true", Toast.LENGTH_LONG);
        } else {
            Log.i("tttt", "shouldShowRequestPermissionRationale false");
            Toast.makeText(getReactApplicationContext(), "shouldShowRequestPermissionRationale false", Toast.LENGTH_LONG);
        }
        ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10010);
    }

    // 请求权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10010:
                Toast.makeText(getReactApplicationContext(), "callback", 3000).show();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    install();
                }
                 else {
                    Uri packageURI = Uri.parse("package:" + getReactApplicationContext().getPackageName());//设置包名，可直接跳转当前软件的设置页面
                    Intent intent = new Intent(ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    getReactApplicationContext().startActivity(intent);
                }
                break;
        }
    }


    // 安装apk
    public void install() {
        if (isEmpty(mAPKPath)) {
            Toast.makeText(getReactApplicationContext(), "apkPath is null", 3000).show();
            return;
        }

        File file = new File(mAPKPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判断版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致  参数3 共享的文件
            Uri apkUri = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() +
                    ".fileProvider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        getCurrentActivity().startActivity(intent);
    }


    public boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }
}
