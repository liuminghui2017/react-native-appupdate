import {
  NativeModules,
  Platform,
} from 'react-native';
import RNFS from 'react-native-fs';

const RNAppUpdate = NativeModules.RNAppUpdate;

// let jobId = -1;

// class AppUpdate {
//   constructor(options) {
//     this.options = options;
//   }

//   GET(url, success, error) {
//     fetch(url)
//       .then((response) => response.json())
//       .then((json) => {
//         success && success(json);
//       })
//       .catch((err) => {
//         error && error(err);
//       });
//   }

//   getApkVersion() {
//     if (jobId !== -1) {
//       return;
//     }
//     if (!this.options.apkVersionUrl) {
//       console.log("apkVersionUrl doesn't exist.");
//       return;
//     }
//     this.GET(this.options.apkVersionUrl, this.getApkVersionSuccess.bind(this), this.getVersionError.bind(this));
//   }

//   getApkVersionSuccess(remote) {
//     console.log("getApkVersionSuccess", remote);
//     if (RNAppUpdate.versionName !== remote.versionName) {
//       if (remote.forceUpdate) {
//         if(this.options.forceUpdateApp) {
//           this.options.forceUpdateApp();
//         }
//         this.downloadApk(remote);
//       } else if (this.options.needUpdateApp) {
//         this.options.needUpdateApp((isUpdate) => {
//           if (isUpdate) {
//             this.downloadApk(remote);
//           }
//         });
//       }
//     } else if(this.options.notNeedUpdateApp) {
//       this.options.notNeedUpdateApp();
//     }
//   }

//   downloadApk(remote) {
//     console.info('invoke', remote)
//     const progress = (data) => {
//       const percentage = ((100 * data.bytesWritten) / data.contentLength) | 0;
//       console.info(percentage)
//       this.options.downloadApkProgress && this.options.downloadApkProgress(percentage);
//     };
//     const begin = (res) => {
//       console.log("downloadApkStart");
//       this.options.downloadApkStart && this.options.downloadApkStart();
//     };
//     const progressDivider = 1;
//     const path = 'upgrade.apk'
//     const downloadDestPath = `${RNFS.ExternalDirectoryPath}/${path}`;

//     const ret = RNFS.downloadFile({
//       fromUrl: remote.apkUrl,
//       toFile: downloadDestPath,
//       begin,
//       progress: this.options.downloadApkProgress,
//       background: true,
//       progressDivider
//     });

//     jobId = ret.jobId;

//     ret.promise.then((res) => {
//       console.log("downloadApkEnd");
//       this.options.downloadApkEnd && this.options.downloadApkEnd();
//       RNAppUpdate.installApk(downloadDestPath);

//       jobId = -1;
//     }).catch((err) => {
//       this.downloadApkError(err);

//       jobId = -1;
//     });
//   }

//   getAppStoreVersion() {
//     if (!this.options.iosAppId) {
//       console.log("iosAppId doesn't exist.");
//       return;
//     }
//     this.GET("https://itunes.apple.com/lookup?id=" + this.options.iosAppId, this.getAppStoreVersionSuccess.bind(this), this.getVersionError.bind(this));
//   }

//   getAppStoreVersionSuccess(data) {
//     if (data.resultCount < 1) {
//       console.log("iosAppId is wrong.");
//       return;
//     }
//     const result = data.results[0];
//     const version = result.version;
//     const trackViewUrl = result.trackViewUrl;
//     if (version !== RNAppUpdate.versionName) {
//       if (this.options.needUpdateApp) {
//         this.options.needUpdateApp((isUpdate) => {
//           if (isUpdate) {
//             RNAppUpdate.installFromAppStore(trackViewUrl);
//           }
//         });
//       }
//     }
//   }

//   getVersionError(err) {
//     console.log("getVersionError", err);
//   }

//   downloadApkError(err) {
//     console.log("downloadApkError", err);
//     this.options.onError && this.options.onError();
//   }

//   checkUpdate() {
//     if (Platform.OS === 'android') {
//       this.getApkVersion();
//     } else {
//       this.getAppStoreVersion();
//     }
//   }
// }


function getAppStoreVersion(iosAppId) {
  const url = "https://itunes.apple.com/lookup?id=" + iosAppId

  return new Promise((resolve) => {
    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        if (data.resultCount < 1) {
          resolve({ code: -1, msg: "iosAppId is wrong" })
        } else {

        }
        resolve({ code: 200, data: data.results[0] })
      })
      .catch((err) => {
        resolve({ code: -2, msg: err })
      });
  })
}

function installFromAppStore(iosAppId) {
  return new Promise((resolve) => {
    getAppStoreVersion(iosAppId).then((res) => {
      if (res.code == 200) {
        const version = res.data.version
        const trackViewUrl = res.data.trackViewUrl
        if (version !== RNAppUpdate.versionName) {
          RNAppUpdate.installFromAppStore(trackViewUrl)
        } else {
          resolve({ code: -3, msg: "trackViewUrl is empty" })
        }    
      } else {
        resolve(res)
      }
    })
  })
}

function installApk(downloadDestPath) {
  RNAppUpdate.installApk(downloadDestPath)
}


export default {
  installApk,
  installFromAppStore,
};
