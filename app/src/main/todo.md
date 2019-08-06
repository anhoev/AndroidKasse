
adb shell dumpsys display | grep mBaseDisplayInfo

adb shell wm density DPI && adb reboot

```
 @Override
    public void onBackPressed() {
        Log.d("Starkasse apk", "onBackPressed Called: Do nothing !!!");
        backPressedTime++;
        if (backPressedTime > 3) {
            super.onBackPressed();
            new Handler().postDelayed(() -> {
                backPressedTime = 0;
            }, 1000 * 60);
        }
    }
```
