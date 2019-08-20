# AmbientControl

An Android application that automatically activates Ambient display when your phone is charging.

## Installation

You have to build it and install it.

There's no use in uploading this to the PlayStore, since it requires
a permission you can only grant through ADB -- and if you're going to grant it, you might as well read the
source code. It's no beauty, and I don't intend to add any more features.

The permission in question:

```
$ adb shell pm grant io.slezica.ambientcontrol android.permission.WRITE_SECURE_SETTINGS
```

Works on my machine (tm).

