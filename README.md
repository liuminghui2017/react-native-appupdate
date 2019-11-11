# React Native AppUpdate
原生程序下载更新插件

## 配置
### Android
#### 1.在AndroidManifest.xml中的application节点下添加provider:
```xml
<provider
  android:name="android.support.v4.content.FileProvider"
  android:authorities="${applicationId}.fileProvider"
  android:exported="false"
  android:grantUriPermissions="true">
  <meta-data
      android:name="android.support.FILE_PROVIDER_PATHS"
      android:resource="@xml/file_paths"/>
</provider>
```

#### 2.在res目录下，新建目录xml，并在xml目录下新建文件file_paths.xml，内容如下:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path path="" name="updateDemo" />
</paths>
```

## 更新
适配android 7.0以上的StrictMode API 政策，使用FileProvider来安装apk文件

## Usage
```javascript
import { Alert } from 'react-native';
import AppUpdate from 'react-native-appupdate';

const appUpdate = new AppUpdate({
  iosAppId: '123456',
  apkVersionUrl: 'https://github.com/version.json',
  needUpdateApp: (needUpdate) => {
    Alert.alert(
      'Update tip',
      'Finding new version, do you want to update?',
      [
        {text: 'Cancel', onPress: () => {}},
        {text: 'Update', onPress: () => needUpdate(true)}
      ]
    );
  },
  forceUpdateApp: () => {
    console.log("Force update will start")
  },
  notNeedUpdateApp: () => {
    console.log("App is up to date")
  },
  downloadApkStart: () => { console.log("Start") },
  downloadApkProgress: (progress) => { console.log(`Downloading ${progress}%...`) },
  downloadApkEnd: () => { console.log("End") },
  onError: () => { console.log("downloadApkError") }
});
appUpdate.checkUpdate();
```

```javascript
// version.json
{
  "versionName":"1.0.0",
  "apkUrl":"https://github.com/NewApp.apk",
  "forceUpdate": false
}
```
## Third Library
* react-native-fs
