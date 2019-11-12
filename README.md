# React Native AppUpdate
原生程序下载更新插件


## 更新
适配android 7.0以上的StrictMode API 政策，使用FileProvider来安装apk文件;
解决FileProvider与三方插件重复问题;

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
