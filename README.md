# React Native AppUpdate
原生程序下载更新插件


## 更新
适配android 7.0以上的StrictMode API 政策，使用FileProvider来安装apk文件;
解决FileProvider与三方插件重复问题;
视频android 8.0增加应用内安装权限;

## Usage
### Android

```javascript
import RNFS from 'react-native-fs';
import AppUpdate from 'react-native-appupdate';

jobId = -1

downloadAndInstall() {
  if (jobId != -1) return

  const progressDivider = 1;
  const downloadDestPath = `${RNFS.ExternalDirectoryPath}/upgrade.apk`;

  const ret = RNFS.downloadFile({
    fromUrl: "apk下载地址",
    toFile: downloadDestPath,
    begin: () => { 
      console.info('begin') 
    },
    progress: ({bytesWritten, contentLength}) => {
      console.info(((100 * bytesWritten) / contentLength).toFixed(0) + '%')
    },
    background: true,
    progressDivider
  });

  jobId = ret.jobId;

  ret.promise.then(() => {
    console.info('download end')
    AppUpdate.installApk(downloadDestPath);
    jobId = -1;
  }).catch((err) => {
    console.info('====>', err)
    jobId = -1;
  });
}
```

### iOS
```javascript
import AppUpdate from 'react-native-appupdate';

AppUpdate.installFromAppStore(iosAppId);
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
