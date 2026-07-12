# AmbientControl

An Android application that automatically activates Ambient display when your phone is charging.

## Installation

You have to build it and install it.

There's no use in uploading this to the PlayStore, since it requires
a permission you can only grant through ADB -- and if you're going to grant it, you might as well read the
source code.

It's no beauty (I think I even left some code half-written) and I don't intend to add any more features. If you find it useful, let me know, I may change my mind.

### Building

You'll need the Android SDK (compile SDK 33) and a device running Android 7.0 (API 24) or later.

```
$ ./gradlew assembleDebug
```

The APK ends up in `app/build/outputs/apk/debug/app-debug.apk`.

### Installing

With USB debugging enabled on your device:

```
$ ./gradlew installDebug
```

Or install the APK directly:

```
$ adb install app/build/outputs/apk/debug/app-debug.apk
```

Then grant the permission in question:

```
$ adb shell pm grant io.slezica.ambientcontrol android.permission.WRITE_SECURE_SETTINGS
```

Works on my machine (tm).

