com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService
service call activity 42 s16 com.android.systemui/.keyguard.KeyguardService
service call activity 42 s16 com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService
pm disable com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService

service call activity 42 s16 com.amazon.firelauncher

am startservice -n com.android.systemui/.ImageWallpaper

adb shell pm grant com.starkasse.kasse android.permission.CONTROL_KEYGUARD

ACTIVITY MANAGER SERVICES (dumpsys activity services)
  User 0 active services:
  * ServiceRecord{56b5998 u0 com.android.systemui/.keyguard.KeyguardService}
    intent={cmp=com.android.systemui/.keyguard.KeyguardService}
    packageName=com.android.systemui
    processName=com.android.systemui
    baseDir=/system/priv-app/SystemUI/SystemUI.apk
    dataDir=/data/data/com.android.systemui
    app=ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}
    createTime=-34s391ms startingBgTimeout=--
    lastActivity=-34s391ms restartTime=-34s391ms createdFromFg=true
    Bindings:
    * IntentBindRecord{30fbc36b CREATE}:
      intent={cmp=com.android.systemui/.keyguard.KeyguardService}
      binder=android.os.BinderProxy@207efec8
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{1e639b61 ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{39e8877b u0 CR com.android.systemui/.keyguard.KeyguardService:@b76030a}
    All Connections:
      ConnectionRecord{39e8877b u0 CR com.android.systemui/.keyguard.KeyguardService:@b76030a}

  * ServiceRecord{3172051 u0 com.android.systemui/.ImageWallpaper}
    intent={act=android.service.wallpaper.WallpaperService cmp=com.android.systemui/.ImageWallpaper}
    packageName=com.android.systemui
    processName=com.android.systemui
    permission=android.permission.BIND_WALLPAPER
    baseDir=/system/priv-app/SystemUI/SystemUI.apk
    dataDir=/data/data/com.android.systemui
    app=ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}
    createTime=-44s816ms startingBgTimeout=--
    lastActivity=-44s816ms restartTime=-44s816ms createdFromFg=true
    Bindings:
    * IntentBindRecord{175d8c9d CREATE}:
      intent={act=android.service.wallpaper.WallpaperService cmp=com.android.systemui/.ImageWallpaper}
      binder=android.os.BinderProxy@121ae512
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{252b34e3 ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{17cac178 u0 CR UI com.android.systemui/.ImageWallpaper:@1ec722db}
    All Connections:
      ConnectionRecord{17cac178 u0 CR UI com.android.systemui/.ImageWallpaper:@1ec722db}

  * ServiceRecord{4fa9a0c u0 com.amazon.client.metrics/.AndroidMetricsServiceAdapter}
    intent={act=com.amazon.client.metrics.bind cmp=com.amazon.client.metrics/.AndroidMetricsServiceAdapter}
    packageName=com.amazon.client.metrics
    processName=com.amazon.client.metrics
    baseDir=/system/priv-app/MetricsService/MetricsService.apk
    dataDir=/data/data/com.amazon.client.metrics
    app=ProcessRecord{1fad478 1566:com.amazon.client.metrics/u0a22032}
    createTime=-44s70ms startingBgTimeout=--
    lastActivity=-5s417ms restartTime=-43s845ms createdFromFg=true
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1
    Bindings:
    * IntentBindRecord{814dee0 CREATE}:
      intent={act=com.amazon.client.metrics.bind cmp=com.amazon.client.metrics/.AndroidMetricsServiceAdapter}
      binder=android.os.BinderProxy@e0b5599
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{3c3e405e ProcessRecord{1fad478 1566:com.amazon.client.metrics/u0a22032}}
        Per-process Connections:
          ConnectionRecord{372a43e u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2a455cf9}
      * Client AppBindRecord{4d23c3f ProcessRecord{5aea5b2 3426:com.android.calendar/u0a22109}}
        Per-process Connections:
          ConnectionRecord{36cf2080 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3b84f903}
      * Client AppBindRecord{3399090c ProcessRecord{63cd9d1 1969:com.amazon.kindle.unifiedSearch/u0a22087}}
        Per-process Connections:
          ConnectionRecord{361b2737 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3aec9c36}
      * Client AppBindRecord{2432b255 ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}}
        Per-process Connections:
          ConnectionRecord{129deeee u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3d196469}
          ConnectionRecord{2f22564f u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2b5538ae}
      * Client AppBindRecord{1eaa046a ProcessRecord{bbf0e31 3244:com.amazon.camera/u0a22005}}
        Per-process Connections:
          ConnectionRecord{30c46297 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@21922916}
      * Client AppBindRecord{2fb85d5b ProcessRecord{1363be2d 3473:com.amazon.kindle.rdmdeviceadmin/u0a22086}}
        Per-process Connections:
          ConnectionRecord{3aa628f3 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2937df62}
      * Client AppBindRecord{d53a9f8 ProcessRecord{198bbc74 2784:com.amazon.whisperlink.core.android/u0a22046}}
        Per-process Connections:
          ConnectionRecord{484e96a u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@c60b55}
          ConnectionRecord{6f22a12 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@29a2c59d}
      * Client AppBindRecord{18391ed1 ProcessRecord{19cfe9e5 1442:com.amazon.tcomm/u0a22102}}
        Per-process Connections:
          ConnectionRecord{9c0c76b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@22e88cba}
      * Client AppBindRecord{1e5bfd36 ProcessRecord{1cabce6a 2277:com.amazon.device.backup/u0a22010}}
        Per-process Connections:
          ConnectionRecord{239383f8 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@1e075f5b}
      * Client AppBindRecord{2157437 ProcessRecord{202fa1db 1458:amazon.speech.sim/u0a22123}}
        Per-process Connections:
          ConnectionRecord{15c6615b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@23dd986a}
      * Client AppBindRecord{6156da4 ProcessRecord{25e63ce4 3366:com.amazon.photos/u0a22094}}
        Per-process Connections:
          ConnectionRecord{30343802 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@5ed954d}
      * Client AppBindRecord{f5fd70d ProcessRecord{2a4b2084 1263:com.amazon.imp/u0a22076}}
        Per-process Connections:
          ConnectionRecord{39c3d4a2 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@ffd406d}
      * Client AppBindRecord{2062b6c2 ProcessRecord{2a5d89fb 1382:com.amazon.media.session.monitor/u0a22123}}
        Per-process Connections:
          ConnectionRecord{10ac71 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3d006a18}
      * Client AppBindRecord{39c31cd3 ProcessRecord{2ad58690 3177:amazon.alexa.tablet/u0a22047}}
        Per-process Connections:
          ConnectionRecord{1f82e78e u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2f4a5b89}
      * Client AppBindRecord{290dc010 ProcessRecord{2aeb6f55 1353:com.amazon.platform/u0a22002}}
        Per-process Connections:
          ConnectionRecord{2d2fe25b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@32e87d6a}
      * Client AppBindRecord{7bfd709 ProcessRecord{303475e2 1509:com.amazon.firelauncher/u0a22069}}
        Per-process Connections:
          ConnectionRecord{54e6430 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@110d3d73}
      * Client AppBindRecord{1a297d0e ProcessRecord{3449efa9 1723:com.amazon.avod/u0a22050}}
        Per-process Connections:
          ConnectionRecord{3b4d0ccf u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@1036b92e}
      * Client AppBindRecord{e98b32f ProcessRecord{37c99daa 3292:com.amazon.webview.metrics.service:AWVMetricsProcess/u0a22003}}
        Per-process Connections:
          ConnectionRecord{17fa0538 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@a5e1d9b}
    All Connections:
      ConnectionRecord{484e96a u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@c60b55}
      ConnectionRecord{30343802 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@5ed954d}
      ConnectionRecord{17fa0538 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@a5e1d9b}
      ConnectionRecord{39c3d4a2 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@ffd406d}
      ConnectionRecord{3b4d0ccf u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@1036b92e}
      ConnectionRecord{54e6430 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@110d3d73}
      ConnectionRecord{239383f8 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@1e075f5b}
      ConnectionRecord{30c46297 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@21922916}
      ConnectionRecord{9c0c76b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@22e88cba}
      ConnectionRecord{15c6615b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@23dd986a}
      ConnectionRecord{3aa628f3 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2937df62}
      ConnectionRecord{6f22a12 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@29a2c59d}
      ConnectionRecord{372a43e u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2a455cf9}
      ConnectionRecord{2f22564f u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2b5538ae}
      ConnectionRecord{1f82e78e u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2f4a5b89}
      ConnectionRecord{2d2fe25b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@32e87d6a}
      ConnectionRecord{361b2737 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3aec9c36}
      ConnectionRecord{36cf2080 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3b84f903}
      ConnectionRecord{10ac71 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3d006a18}
      ConnectionRecord{129deeee u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3d196469}

  * ServiceRecord{273dc1a7 u0 amazon.alexa.tablet/amazon.alexa.alerts.service.VolumeChangeService}
    intent={cmp=amazon.alexa.tablet/amazon.alexa.alerts.service.VolumeChangeService}
    packageName=amazon.alexa.tablet
    processName=amazon.alexa.tablet
    baseDir=/data/app/amazon.alexa.tablet-1/base.apk
    dataDir=/data/data/amazon.alexa.tablet
    app=ProcessRecord{2ad58690 3177:amazon.alexa.tablet/u0a22047}
    createTime=-20s894ms startingBgTimeout=-5s893ms
    lastActivity=-20s894ms restartTime=-20s894ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1

  * ServiceRecord{36c6a015 u0 com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService}
    intent={cmp=com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService}
    packageName=com.android.systemui
    processName=com.android.systemui
    permission=android.permission.CONTROL_KEYGUARD
    baseDir=/system/priv-app/SystemUI/SystemUI.apk
    dataDir=/data/data/com.android.systemui
    app=ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}
    createTime=-41s194ms startingBgTimeout=--
    lastActivity=-20s914ms restartTime=-41s194ms createdFromFg=false
    Bindings:
    * IntentBindRecord{2f6cd3c CREATE}:
      intent={cmp=com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService}
      binder=android.os.BinderProxy@2e05dac5
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{1a845c1a ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}}
        Per-process Connections:
          ConnectionRecord{26f9316 u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@1d67e031}
      * Client AppBindRecord{3487534b ProcessRecord{202fa1db 1458:amazon.speech.sim/u0a22123}}
        Per-process Connections:
          ConnectionRecord{22f515cc u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@2a9783ff}
      * Client AppBindRecord{10018128 ProcessRecord{2ad58690 3177:amazon.alexa.tablet/u0a22047}}
        Per-process Connections:
          ConnectionRecord{37b4f2c1 u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@2d2b97a8}
    All Connections:
      ConnectionRecord{26f9316 u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@1d67e031}
      ConnectionRecord{22f515cc u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@2a9783ff}
      ConnectionRecord{37b4f2c1 u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@2d2b97a8}

  * ServiceRecord{1e6b7c47 u0 com.amazon.whisperlink.core.android/.WhisperLinkCoreService}
    intent={cmp=com.amazon.whisperlink.core.android/.WhisperLinkCoreService}
    packageName=com.amazon.whisperlink.core.android
    processName=com.amazon.whisperlink.core.android
    baseDir=/system/priv-app/WhisperplayCore/WhisperplayCore.apk
    dataDir=/data/data/com.amazon.whisperlink.core.android
    app=ProcessRecord{198bbc74 2784:com.amazon.whisperlink.core.android/u0a22046}
    createTime=-25s883ms startingBgTimeout=-10s881ms
    lastActivity=-5s685ms restartTime=-25s883ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=2
    Bindings:
    * IntentBindRecord{12ee5e41 CREATE}:
      intent={cmp=com.amazon.whisperlink.core.android/.WhisperLinkCoreService}
      binder=android.os.BinderProxy@177c1fe6
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{2375d927 ProcessRecord{198bbc74 2784:com.amazon.whisperlink.core.android/u0a22046}}
        Per-process Connections:
          ConnectionRecord{d6e260c u0 CR OOM com.amazon.whisperlink.core.android/.WhisperLinkCoreService:@c85ad3f}
    All Connections:
      ConnectionRecord{d6e260c u0 CR OOM com.amazon.whisperlink.core.android/.WhisperLinkCoreService:@c85ad3f}

  * ServiceRecord{3d872fb4 u0 com.wparam.nullkeyboard/.NullKeyboard}
    intent={act=android.view.InputMethod cmp=com.wparam.nullkeyboard/.NullKeyboard}
    packageName=com.wparam.nullkeyboard
    processName=com.wparam.nullkeyboard
    permission=android.permission.BIND_INPUT_METHOD
    baseDir=/data/app/com.wparam.nullkeyboard-1/base.apk
    dataDir=/data/data/com.wparam.nullkeyboard
    app=ProcessRecord{1c8187d4 1316:com.wparam.nullkeyboard/u0a17}
    createTime=-44s798ms startingBgTimeout=--
    lastActivity=-44s624ms restartTime=-44s624ms createdFromFg=true
    Bindings:
    * IntentBindRecord{3c819d7d CREATE}:
      intent={act=android.view.InputMethod cmp=com.wparam.nullkeyboard/.NullKeyboard}
      binder=android.os.BinderProxy@cda5472
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{dce0c3 ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{8d6a79a u0 CR !FG UI !VIS com.wparam.nullkeyboard/.NullKeyboard:@f4c2445}
    All Connections:
      ConnectionRecord{8d6a79a u0 CR !FG UI !VIS com.wparam.nullkeyboard/.NullKeyboard:@f4c2445}

  * ServiceRecord{4594d40 u0 com.android.bluetooth/.a2dp.A2dpService}
    intent={act=android.bluetooth.IBluetoothA2dp cmp=com.android.bluetooth/.a2dp.A2dpService}
    packageName=com.android.bluetooth
    processName=com.android.bluetooth
    baseDir=/system/app/Bluetooth/Bluetooth.apk
    dataDir=/data/data/com.android.bluetooth
    app=null
    createTime=-44s966ms startingBgTimeout=--
    lastActivity=-44s966ms restartTime=-- createdFromFg=true
    Bindings:
    * IntentBindRecord{f0f9479}:
      intent={act=android.bluetooth.IBluetoothA2dp cmp=com.android.bluetooth/.a2dp.A2dpService}
      binder=null
      requested=false received=false hasBound=false doRebind=false
      * Client AppBindRecord{377545be ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}}
        Per-process Connections:
          ConnectionRecord{3e9199be u0 com.android.bluetooth/.a2dp.A2dpService:@1258b879}
      * Client AppBindRecord{722c61f ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{3ce61571 u0 com.android.bluetooth/.a2dp.A2dpService:@595d718}
    All Connections:
      ConnectionRecord{3ce61571 u0 com.android.bluetooth/.a2dp.A2dpService:@595d718}
      ConnectionRecord{3e9199be u0 com.android.bluetooth/.a2dp.A2dpService:@1258b879}

  * ServiceRecord{926727c u0 com.android.systemui/.SystemUIService}
    intent={cmp=com.android.systemui/.SystemUIService}
    packageName=com.android.systemui
    processName=com.android.systemui
    baseDir=/system/priv-app/SystemUI/SystemUI.apk
    dataDir=/data/data/com.android.systemui
    app=ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}
    createTime=-45s696ms startingBgTimeout=--
    lastActivity=-45s574ms restartTime=-45s574ms createdFromFg=true
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1

  * ServiceRecord{1ccba595 u0 android/.hardware.location.GeofenceHardwareService}
    intent={cmp=android/.hardware.location.GeofenceHardwareService}
    packageName=android
    processName=system
    permission=android.permission.LOCATION_HARDWARE
    baseDir=/system/framework/framework-res.apk
    dataDir=/data/system
    app=ProcessRecord{288889c4 975:system/1000}
    createTime=-44s608ms startingBgTimeout=--
    lastActivity=-44s608ms restartTime=-44s608ms createdFromFg=true
    Bindings:
    * IntentBindRecord{3d25fd6c CREATE}:
      intent={cmp=android/.hardware.location.GeofenceHardwareService}
      binder=android.hardware.location.GeofenceHardwareService$1@9ebff35
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{393bffca ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{387cc14c u0 CR android/.hardware.location.GeofenceHardwareService:@230cad7f}
    All Connections:
      ConnectionRecord{387cc14c u0 CR android/.hardware.location.GeofenceHardwareService:@230cad7f}

  * ServiceRecord{39f5cfb5 u0 com.amazon.platform/com.amazon.geo.platform.GeoDeviceService}
    intent={act=com.android.location.service.GeocodeProvider pkg=com.amazon.platform}
    packageName=com.amazon.platform
    processName=com.amazon.platform
    baseDir=/system/priv-app/AmazonPlatform-release/AmazonPlatform-release.apk
    dataDir=/data/data/com.amazon.platform
    app=ProcessRecord{2aeb6f55 1353:com.amazon.platform/u0a22002}
    createTime=-42s341ms startingBgTimeout=--
    lastActivity=-42s341ms restartTime=-42s341ms createdFromFg=true
    Bindings:
    * IntentBindRecord{6b7a53b CREATE}:
      intent={act=com.android.location.service.GeocodeProvider pkg=com.amazon.platform}
      binder=android.os.BinderProxy@172b8458
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{13ea59b1 ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{1feabfec u0 CR !FG !VIS com.amazon.platform/com.amazon.geo.platform.GeoDeviceService:@2bcf029f}
    All Connections:
      ConnectionRecord{1feabfec u0 CR !FG !VIS com.amazon.platform/com.amazon.geo.platform.GeoDeviceService:@2bcf029f}

  * ServiceRecord{11a0e7eb u0 com.amazon.tcomm/com.amazon.communication.AndroidTCommService}
    intent={cmp=com.amazon.tcomm/com.amazon.communication.AndroidTCommService}
    packageName=com.amazon.tcomm
    processName=com.amazon.tcomm
    permission=amazon.permission.USE_TCOMM
    baseDir=/system/priv-app/com.amazon.tcomm/com.amazon.tcomm.apk
    dataDir=/data/data/com.amazon.tcomm
    app=ProcessRecord{19cfe9e5 1442:com.amazon.tcomm/u0a22102}
    createTime=-42s820ms startingBgTimeout=--
    lastActivity=-24s325ms restartTime=-42s819ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1
    Bindings:
    * IntentBindRecord{32024e96 CREATE}:
      intent={act=com.amazon.communication.TCOMM cmp=com.amazon.tcomm/com.amazon.communication.AndroidTCommService}
      binder=android.os.BinderProxy@1cf15a17
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{21008e04 ProcessRecord{198bbc74 2784:com.amazon.whisperlink.core.android/u0a22046}}
        Per-process Connections:
          ConnectionRecord{17484d10 u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@1b4bbdd3}
          ConnectionRecord{1d85a63b u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@c6d64ca}
      * Client AppBindRecord{1399dfed ProcessRecord{19cfe9e5 1442:com.amazon.tcomm/u0a22102}}
        Per-process Connections:
          ConnectionRecord{1c05887b u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@19d2680a}
    All Connections:
      ConnectionRecord{1d85a63b u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@c6d64ca}
      ConnectionRecord{1c05887b u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@19d2680a}
      ConnectionRecord{17484d10 u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@1b4bbdd3}

  * ServiceRecord{b392c3d u0 com.amazon.media.session.monitor/.MediaSessionMonitorService}
    intent={cmp=com.amazon.media.session.monitor/.MediaSessionMonitorService}
    packageName=com.amazon.media.session.monitor
    processName=com.amazon.media.session.monitor
    baseDir=/system/priv-app/com.amazon.media.session.monitor/com.amazon.media.session.monitor.apk
    dataDir=/data/data/com.amazon.media.session.monitor
    app=ProcessRecord{2a5d89fb 1382:com.amazon.media.session.monitor/u0a22123}
    createTime=-42s997ms startingBgTimeout=--
    lastActivity=-42s997ms restartTime=-42s997ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1

  * ServiceRecord{961e1b9 u0 com.amazon.knight.speechui/.SpeechUiService}
    intent={cmp=com.amazon.knight.speechui/.SpeechUiService}
    packageName=com.amazon.knight.speechui
    processName=com.android.systemui
    permission=amazon.speech.permission.SEND_ALEXA_DIRECTIVE
    baseDir=/system/priv-app/SpeechUiTablet/SpeechUiTablet.apk
    dataDir=/data/data/com.amazon.knight.speechui
    app=ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}
    createTime=-40s252ms startingBgTimeout=--
    lastActivity=-36s182ms restartTime=-40s252ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=true callStart=true lastStartId=1
    Bindings:
    * IntentBindRecord{190cbe22 CREATE}:
      intent={cmp=com.amazon.knight.speechui/.SpeechUiService}
      binder=android.os.BinderProxy@3ca780b3
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{19fa8670 ProcessRecord{202fa1db 1458:amazon.speech.sim/u0a22123}}
        Per-process Connections:
          ConnectionRecord{26fb8780 u0 CR com.amazon.knight.speechui/.SpeechUiService:@2e969c03}
    All Connections:
      ConnectionRecord{26fb8780 u0 CR com.amazon.knight.speechui/.SpeechUiService:@2e969c03}

  * ServiceRecord{3a8438f9 u0 com.teamviewer.host.market/com.teamviewer.host.application.NetworkServiceHost}
    intent={cmp=com.teamviewer.host.market/com.teamviewer.host.application.NetworkServiceHost}
    packageName=com.teamviewer.host.market
    processName=com.teamviewer.host.market
    baseDir=/data/app/com.teamviewer.host.market-1/base.apk
    dataDir=/data/data/com.teamviewer.host.market
    app=ProcessRecord{2c5cf9fd 3209:com.teamviewer.host.market/u0a15}
    createTime=-19s235ms startingBgTimeout=-4s233ms
    lastActivity=-19s235ms restartTime=-19s235ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1

  * ServiceRecord{3ab3874f u0 com.android.providers.media/.MtpService}
    intent={cmp=com.android.providers.media/.MtpService}
    packageName=com.android.providers.media
    processName=android.process.media
    baseDir=/system/priv-app/MediaProvider/MediaProvider.apk
    dataDir=/data/data/com.android.providers.media
    app=ProcessRecord{27c4dedc 1124:android.process.media/u0a22030}
    createTime=-45s257ms startingBgTimeout=-30s256ms
    lastActivity=-26s400ms restartTime=-45s257ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=2
    Bindings:
    * IntentBindRecord{f418de9 CREATE}:
      intent={cmp=com.android.providers.media/.MtpService}
      binder=android.os.BinderProxy@235c9a6e
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{248f750f ProcessRecord{27c4dedc 1124:android.process.media/u0a22030}}
        Per-process Connections:
          ConnectionRecord{10e3d6ba u0 CR com.android.providers.media/.MtpService:@24e21be5}
    All Connections:
      ConnectionRecord{10e3d6ba u0 CR com.android.providers.media/.MtpService:@24e21be5}

  * ServiceRecord{2b3d8ddc u0 com.amazon.whisperlink.activityview.android/.service.NotificationService}
    intent={act=android.intent.action.BOOT_COMPLETED cmp=com.amazon.whisperlink.activityview.android/.service.NotificationService}
    packageName=com.amazon.whisperlink.activityview.android
    processName=com.amazon.whisperlink.core.android
    permission=com.amazon.whisperlink.activityview.LAUNCH
    baseDir=/system/priv-app/WhisperplayActivityView/WhisperplayActivityView.apk
    dataDir=/data/data/com.amazon.whisperlink.activityview.android
    app=ProcessRecord{198bbc74 2784:com.amazon.whisperlink.core.android/u0a22046}
    createTime=-25s925ms startingBgTimeout=-10s923ms
    lastActivity=-5s703ms restartTime=-25s925ms createdFromFg=false
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=2

  * ServiceRecord{2a473e12 u0 amazon.speech.sim/.service.SpeechInteractionService}
    intent={act=amazon.speech.intent.action.GET_SIMCLIENT cmp=amazon.speech.sim/.service.SpeechInteractionService}
    packageName=amazon.speech.sim
    processName=amazon.speech.sim
    permission=amazon.speech.permission.CONNECT_WITH_SIM
    baseDir=/system/priv-app/SpeechInteractionManager/SpeechInteractionManager.apk
    dataDir=/data/data/amazon.speech.sim
    app=ProcessRecord{202fa1db 1458:amazon.speech.sim/u0a22123}
    createTime=-44s50ms startingBgTimeout=--
    lastActivity=-21s40ms restartTime=-44s50ms createdFromFg=true
    startRequested=true delayedStop=false stopIfKilled=false callStart=true lastStartId=1
    Bindings:
    * IntentBindRecord{1f59999c CREATE}:
      intent={act=amazon.speech.intent.action.GET_SIMCLIENT cmp=amazon.speech.sim/.service.SpeechInteractionService}
      binder=android.os.BinderProxy@109c1fa5
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{2bbef7a ProcessRecord{9dcbd29 1097:com.android.systemui/u0a22042}}
        Per-process Connections:
          ConnectionRecord{17d68e6 u0 CR amazon.speech.sim/.service.SpeechInteractionService:@14732b41}
          ConnectionRecord{6fa421c u0 CR amazon.speech.sim/.service.SpeechInteractionService:@61f6f8f}
          ConnectionRecord{bcea60e u0 CR amazon.speech.sim/.service.SpeechInteractionService:@1b8c0409}
          ConnectionRecord{278fa99d u0 CR amazon.speech.sim/.service.SpeechInteractionService:@14db074}
          ConnectionRecord{396b713f u0 CR amazon.speech.sim/.service.SpeechInteractionService:@2d57295e}
      * Client AppBindRecord{1f58532b ProcessRecord{2a5d89fb 1382:com.amazon.media.session.monitor/u0a22123}}
        Per-process Connections:
          ConnectionRecord{2fb5f665 u0 CR amazon.speech.sim/.service.SpeechInteractionService:@d6d935c}
      * Client AppBindRecord{34b388 ProcessRecord{2ad58690 3177:amazon.alexa.tablet/u0a22047}}
        Per-process Connections:
          ConnectionRecord{23b47bbc u0 CR amazon.speech.sim/.service.SpeechInteractionService:@331e0baf}
    All Connections:
      ConnectionRecord{278fa99d u0 CR amazon.speech.sim/.service.SpeechInteractionService:@14db074}
      ConnectionRecord{6fa421c u0 CR amazon.speech.sim/.service.SpeechInteractionService:@61f6f8f}
      ConnectionRecord{2fb5f665 u0 CR amazon.speech.sim/.service.SpeechInteractionService:@d6d935c}
      ConnectionRecord{17d68e6 u0 CR amazon.speech.sim/.service.SpeechInteractionService:@14732b41}
      ConnectionRecord{bcea60e u0 CR amazon.speech.sim/.service.SpeechInteractionService:@1b8c0409}
      ConnectionRecord{396b713f u0 CR amazon.speech.sim/.service.SpeechInteractionService:@2d57295e}
      ConnectionRecord{23b47bbc u0 CR amazon.speech.sim/.service.SpeechInteractionService:@331e0baf}

  * ServiceRecord{31e29133 u0 com.amazon.webview.metrics.service/.AWVMetricsService}
    intent={cmp=com.amazon.webview.metrics.service/.AWVMetricsService}
    packageName=com.amazon.webview.metrics.service
    processName=com.amazon.webview.metrics.service:AWVMetricsProcess
    baseDir=/system/priv-app/AwvMetricsService/AwvMetricsService.apk
    dataDir=/data/data/com.amazon.webview.metrics.service
    app=ProcessRecord{37c99daa 3292:com.amazon.webview.metrics.service:AWVMetricsProcess/u0a22003}
    createTime=-17s975ms startingBgTimeout=--
    lastActivity=-17s937ms restartTime=-17s937ms createdFromFg=false
    Bindings:
    * IntentBindRecord{8541121 CREATE}:
      intent={cmp=com.amazon.webview.metrics.service/.AWVMetricsService}
      binder=android.os.BinderProxy@9898946
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{2986f707 ProcessRecord{31722c84 3265:com.amazon.windowshop/u0a22107}}
        Per-process Connections:
          ConnectionRecord{3dffc0a2 u0 CR com.amazon.webview.metrics.service/.AWVMetricsService:@9455c6d}
    All Connections:
      ConnectionRecord{3dffc0a2 u0 CR com.amazon.webview.metrics.service/.AWVMetricsService:@9455c6d}

  * ServiceRecord{28423633 u0 com.android.location.fused/.FusedLocationService}
    intent={act=com.android.location.service.FusedLocationProvider pkg=com.android.location.fused}
    packageName=com.android.location.fused
    processName=system
    permission=android.permission.WRITE_SECURE_SETTINGS
    baseDir=/system/priv-app/FusedLocation/FusedLocation.apk
    dataDir=/data/data/com.android.location.fused
    app=ProcessRecord{288889c4 975:system/1000}
    createTime=-44s702ms startingBgTimeout=--
    lastActivity=-44s702ms restartTime=-44s702ms createdFromFg=true
    Bindings:
    * IntentBindRecord{24258034 CREATE}:
      intent={act=com.android.location.service.FusedLocationProvider pkg=com.android.location.fused}
      binder=com.android.location.provider.LocationProviderBase$Service@2d3f9e5d
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{3f44f3d2 ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{3b32d9a2 u0 CR !FG !VIS com.android.location.fused/.FusedLocationService:@2761396d}
    All Connections:
      ConnectionRecord{3b32d9a2 u0 CR !FG !VIS com.android.location.fused/.FusedLocationService:@2761396d}

  * ServiceRecord{443e0f1 u0 com.here.odnp.service/.LocationService}
    intent={act=com.android.location.service.v3.NetworkLocationProvider pkg=com.here.odnp.service}
    packageName=com.here.odnp.service
    processName=com.here.odnp.service:remote
    permission=android.permission.WRITE_SECURE_SETTINGS
    baseDir=/system/priv-app/com.nokia.odnp.service/com.nokia.odnp.service.apk
    dataDir=/data/data/com.here.odnp.service
    app=ProcessRecord{1911fca3 1338:com.here.odnp.service:remote/1000}
    createTime=-44s729ms startingBgTimeout=--
    lastActivity=-44s218ms restartTime=-44s218ms createdFromFg=true
    Bindings:
    * IntentBindRecord{30b46ba0 CREATE}:
      intent={act=com.android.location.service.v3.NetworkLocationProvider pkg=com.here.odnp.service}
      binder=android.os.BinderProxy@135cc359
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{31da7b1e ProcessRecord{288889c4 975:system/1000}}
        Per-process Connections:
          ConnectionRecord{21972abb u0 CR !FG !VIS com.here.odnp.service/.LocationService:@3c782b4a}
    All Connections:
      ConnectionRecord{21972abb u0 CR !FG !VIS com.here.odnp.service/.LocationService:@3c782b4a}

  * ServiceRecord{1bb0a070 u0 com.amazon.kindle.unifiedSearch/.service.PrewarmerService}
    intent={cmp=com.amazon.kindle.unifiedSearch/.service.PrewarmerService}
    packageName=com.amazon.kindle.unifiedSearch
    processName=com.amazon.kindle.unifiedSearch
    permission=com.amazon.kindle.unifiedSearch.PRE_WARM
    baseDir=/system/priv-app/com.amazon.kindle.unifiedSearch/com.amazon.kindle.unifiedSearch.apk
    dataDir=/data/data/com.amazon.kindle.unifiedSearch
    app=ProcessRecord{63cd9d1 1969:com.amazon.kindle.unifiedSearch/u0a22087}
    createTime=-30s924ms startingBgTimeout=--
    lastActivity=-30s924ms restartTime=-30s924ms createdFromFg=true
    Bindings:
    * IntentBindRecord{22bdbfff CREATE}:
      intent={cmp=com.amazon.kindle.unifiedSearch/.service.PrewarmerService}
      binder=android.os.BinderProxy@1984a1cc
      requested=true received=true hasBound=true doRebind=false
      * Client AppBindRecord{368d3c15 ProcessRecord{303475e2 1509:com.amazon.firelauncher/u0a22069}}
        Per-process Connections:
          ConnectionRecord{1d5dc2b3 u0 CR com.amazon.kindle.unifiedSearch/.service.PrewarmerService:@18ac822}
    All Connections:
      ConnectionRecord{1d5dc2b3 u0 CR com.amazon.kindle.unifiedSearch/.service.PrewarmerService:@18ac822}

  Connection bindings to services:
  * ConnectionRecord{484e96a u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@c60b55}
    binding=AppBindRecord{d53a9f8 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.whisperlink.core.android}
    conn=android.os.BinderProxy@c60b55 flags=0x11
  * ConnectionRecord{278fa99d u0 CR amazon.speech.sim/.service.SpeechInteractionService:@14db074}
    binding=AppBindRecord{2bbef7a amazon.speech.sim/.service.SpeechInteractionService:com.android.systemui}
    conn=android.os.BinderProxy@14db074 flags=0x1
  * ConnectionRecord{1d5dc2b3 u0 CR com.amazon.kindle.unifiedSearch/.service.PrewarmerService:@18ac822}
    binding=AppBindRecord{368d3c15 com.amazon.kindle.unifiedSearch/.service.PrewarmerService:com.amazon.firelauncher}
    activity=ActivityRecord{3b6f320a u0 com.amazon.firelauncher/.Launcher t71}
    conn=android.os.BinderProxy@18ac822 flags=0x1
  * ConnectionRecord{3ce61571 u0 com.android.bluetooth/.a2dp.A2dpService:@595d718}
    binding=AppBindRecord{722c61f com.android.bluetooth/.a2dp.A2dpService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@595d718 flags=0x0
  * ConnectionRecord{30343802 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@5ed954d}
    binding=AppBindRecord{6156da4 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.photos}
    conn=android.os.BinderProxy@5ed954d flags=0x11
  * ConnectionRecord{6fa421c u0 CR amazon.speech.sim/.service.SpeechInteractionService:@61f6f8f}
    binding=AppBindRecord{2bbef7a amazon.speech.sim/.service.SpeechInteractionService:com.android.systemui}
    conn=android.os.BinderProxy@61f6f8f flags=0x1
  * ConnectionRecord{3dffc0a2 u0 CR com.amazon.webview.metrics.service/.AWVMetricsService:@9455c6d}
    binding=AppBindRecord{2986f707 com.amazon.webview.metrics.service/.AWVMetricsService:com.amazon.windowshop}
    conn=android.os.BinderProxy@9455c6d flags=0x1
  * ConnectionRecord{17fa0538 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@a5e1d9b}
    binding=AppBindRecord{e98b32f com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.webview.metrics.service:AWVMetricsProcess}
    conn=android.os.BinderProxy@a5e1d9b flags=0x11
  * ConnectionRecord{39e8877b u0 CR com.android.systemui/.keyguard.KeyguardService:@b76030a}
    binding=AppBindRecord{1e639b61 com.android.systemui/.keyguard.KeyguardService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@b76030a flags=0x1
  * ConnectionRecord{1d85a63b u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@c6d64ca}
    binding=AppBindRecord{21008e04 com.amazon.tcomm/com.amazon.communication.AndroidTCommService:com.amazon.whisperlink.core.android}
    conn=android.os.BinderProxy@c6d64ca flags=0x5
  * ConnectionRecord{d6e260c u0 CR OOM com.amazon.whisperlink.core.android/.WhisperLinkCoreService:@c85ad3f}
    binding=AppBindRecord{2375d927 com.amazon.whisperlink.core.android/.WhisperLinkCoreService:com.amazon.whisperlink.core.android}
    conn=android.os.BinderProxy@c85ad3f flags=0x11
  * ConnectionRecord{196d7f73 u0 CR com.amazon.device.backup/.transport.BackupTransportService:@cc47fe2}
    binding=AppBindRecord{27e93f74 com.amazon.device.backup/.transport.BackupTransportService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@cc47fe2 flags=0x1
  * ConnectionRecord{f3a3b8e u0 CR com.amazon.device.backup/.transport.BackupTransportService:@cc47fe2}
    binding=AppBindRecord{27e93f74 com.amazon.device.backup/.transport.BackupTransportService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@cc47fe2 flags=0x1
  * ConnectionRecord{2fb5f665 u0 CR amazon.speech.sim/.service.SpeechInteractionService:@d6d935c}
    binding=AppBindRecord{1f58532b amazon.speech.sim/.service.SpeechInteractionService:com.amazon.media.session.monitor}
    conn=android.os.BinderProxy@d6d935c flags=0x1
  * ConnectionRecord{8d6a79a u0 CR !FG UI !VIS com.wparam.nullkeyboard/.NullKeyboard:@f4c2445}
    binding=AppBindRecord{dce0c3 com.wparam.nullkeyboard/.NullKeyboard:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@f4c2445 flags=0x60000005
  * ConnectionRecord{39c3d4a2 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@ffd406d}
    binding=AppBindRecord{f5fd70d com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.imp}
    conn=android.os.BinderProxy@ffd406d flags=0x11
  * ConnectionRecord{3b4d0ccf u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@1036b92e}
    binding=AppBindRecord{1a297d0e com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.avod}
    conn=android.os.BinderProxy@1036b92e flags=0x11
  * ConnectionRecord{54e6430 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@110d3d73}
    binding=AppBindRecord{7bfd709 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.firelauncher}
    conn=android.os.BinderProxy@110d3d73 flags=0x11
  * ConnectionRecord{3e9199be u0 com.android.bluetooth/.a2dp.A2dpService:@1258b879}
    binding=AppBindRecord{377545be com.android.bluetooth/.a2dp.A2dpService:com.android.systemui}
    conn=android.os.BinderProxy@1258b879 flags=0x0
  * ConnectionRecord{17d68e6 u0 CR amazon.speech.sim/.service.SpeechInteractionService:@14732b41}
    binding=AppBindRecord{2bbef7a amazon.speech.sim/.service.SpeechInteractionService:com.android.systemui}
    conn=android.os.BinderProxy@14732b41 flags=0x1
  * ConnectionRecord{1c05887b u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@19d2680a}
    binding=AppBindRecord{1399dfed com.amazon.tcomm/com.amazon.communication.AndroidTCommService:com.amazon.tcomm}
    conn=android.os.BinderProxy@19d2680a flags=0x5
  * ConnectionRecord{17484d10 u0 CR !FG com.amazon.tcomm/com.amazon.communication.AndroidTCommService:@1b4bbdd3}
    binding=AppBindRecord{21008e04 com.amazon.tcomm/com.amazon.communication.AndroidTCommService:com.amazon.whisperlink.core.android}
    conn=android.os.BinderProxy@1b4bbdd3 flags=0x5
  * ConnectionRecord{bcea60e u0 CR amazon.speech.sim/.service.SpeechInteractionService:@1b8c0409}
    binding=AppBindRecord{2bbef7a amazon.speech.sim/.service.SpeechInteractionService:com.android.systemui}
    conn=android.os.BinderProxy@1b8c0409 flags=0x1
  * ConnectionRecord{26f9316 u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@1d67e031}
    binding=AppBindRecord{1a845c1a com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:com.android.systemui}
    conn=android.os.BinderProxy@1d67e031 flags=0x1
  * ConnectionRecord{239383f8 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@1e075f5b}
    binding=AppBindRecord{1e5bfd36 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.device.backup}
    conn=android.os.BinderProxy@1e075f5b flags=0x11
  * ConnectionRecord{17cac178 u0 CR UI com.android.systemui/.ImageWallpaper:@1ec722db}
    binding=AppBindRecord{252b34e3 com.android.systemui/.ImageWallpaper:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@1ec722db flags=0x20000001
  * ConnectionRecord{30c46297 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@21922916}
    binding=AppBindRecord{1eaa046a com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.camera}
    conn=android.os.BinderProxy@21922916 flags=0x11
  * ConnectionRecord{9c0c76b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@22e88cba}
    binding=AppBindRecord{18391ed1 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.tcomm}
    conn=android.os.BinderProxy@22e88cba flags=0x11
  * ConnectionRecord{387cc14c u0 CR android/.hardware.location.GeofenceHardwareService:@230cad7f}
    binding=AppBindRecord{393bffca android/.hardware.location.GeofenceHardwareService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@230cad7f flags=0x1
  * ConnectionRecord{15c6615b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@23dd986a}
    binding=AppBindRecord{2157437 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:amazon.speech.sim}
    conn=android.os.BinderProxy@23dd986a flags=0x11
  * ConnectionRecord{10e3d6ba u0 CR com.android.providers.media/.MtpService:@24e21be5}
    binding=AppBindRecord{248f750f com.android.providers.media/.MtpService:android.process.media}
    conn=android.os.BinderProxy@24e21be5 flags=0x1
  * ConnectionRecord{3b32d9a2 u0 CR !FG !VIS com.android.location.fused/.FusedLocationService:@2761396d}
    binding=AppBindRecord{3f44f3d2 com.android.location.fused/.FusedLocationService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@2761396d flags=0x40000005
  * ConnectionRecord{14ca5ede u0 CR !FG com.amazon.avod/.playbackclient.sdk.PlaybackSdkService:@27d2de19}
    binding=AppBindRecord{3f7b85e5 com.amazon.avod/.playbackclient.sdk.PlaybackSdkService:com.amazon.firelauncher}
    conn=android.os.BinderProxy@27d2de19 flags=0x5
  * ConnectionRecord{3aa628f3 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2937df62}
    binding=AppBindRecord{2fb85d5b com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.kindle.rdmdeviceadmin}
    conn=android.os.BinderProxy@2937df62 flags=0x11
  * ConnectionRecord{6f22a12 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@29a2c59d}
    binding=AppBindRecord{d53a9f8 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.whisperlink.core.android}
    conn=android.os.BinderProxy@29a2c59d flags=0x11
  * ConnectionRecord{372a43e u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2a455cf9}
    binding=AppBindRecord{3c3e405e com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.client.metrics}
    conn=android.os.BinderProxy@2a455cf9 flags=0x11
  * ConnectionRecord{6018204 u0 com.android.bluetooth/.hid.HidService:@2a4d9e17}
    binding=AppBindRecord{23388fae com.android.bluetooth/.hid.HidService:com.android.systemui}
    conn=android.os.BinderProxy@2a4d9e17 flags=0x0
  * ConnectionRecord{22f515cc u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@2a9783ff}
    binding=AppBindRecord{3487534b com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:amazon.speech.sim}
    conn=android.os.BinderProxy@2a9783ff flags=0x1
  * ConnectionRecord{2f22564f u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2b5538ae}
    binding=AppBindRecord{2432b255 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.android.systemui}
    conn=android.os.BinderProxy@2b5538ae flags=0x11
  * ConnectionRecord{1feabfec u0 CR !FG !VIS com.amazon.platform/com.amazon.geo.platform.GeoDeviceService:@2bcf029f}
    binding=AppBindRecord{13ea59b1 com.amazon.platform/com.amazon.geo.platform.GeoDeviceService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@2bcf029f flags=0x40000005
  * ConnectionRecord{37b4f2c1 u0 CR com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:@2d2b97a8}
    binding=AppBindRecord{10018128 com.android.systemui/com.amazon.systemui.keyguard.AmazonKeyguardService:amazon.alexa.tablet}
    conn=android.os.BinderProxy@2d2b97a8 flags=0x1
  * ConnectionRecord{396b713f u0 CR amazon.speech.sim/.service.SpeechInteractionService:@2d57295e}
    binding=AppBindRecord{2bbef7a amazon.speech.sim/.service.SpeechInteractionService:com.android.systemui}
    conn=android.os.BinderProxy@2d57295e flags=0x1
  * ConnectionRecord{26fb8780 u0 CR com.amazon.knight.speechui/.SpeechUiService:@2e969c03}
    binding=AppBindRecord{19fa8670 com.amazon.knight.speechui/.SpeechUiService:amazon.speech.sim}
    conn=android.os.BinderProxy@2e969c03 flags=0x1
  * ConnectionRecord{1f82e78e u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@2f4a5b89}
    binding=AppBindRecord{39c31cd3 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:amazon.alexa.tablet}
    conn=android.os.BinderProxy@2f4a5b89 flags=0x11
  * ConnectionRecord{2d2fe25b u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@32e87d6a}
    binding=AppBindRecord{290dc010 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.platform}
    conn=android.os.BinderProxy@32e87d6a flags=0x11
  * ConnectionRecord{23b47bbc u0 CR amazon.speech.sim/.service.SpeechInteractionService:@331e0baf}
    binding=AppBindRecord{34b388 amazon.speech.sim/.service.SpeechInteractionService:amazon.alexa.tablet}
    conn=android.os.BinderProxy@331e0baf flags=0x1
  * ConnectionRecord{361b2737 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3aec9c36}
    binding=AppBindRecord{3399090c com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.kindle.unifiedSearch}
    conn=android.os.BinderProxy@3aec9c36 flags=0x11
  * ConnectionRecord{36cf2080 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3b84f903}
    binding=AppBindRecord{4d23c3f com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.android.calendar}
    conn=android.os.BinderProxy@3b84f903 flags=0x11
  * ConnectionRecord{21972abb u0 CR !FG !VIS com.here.odnp.service/.LocationService:@3c782b4a}
    binding=AppBindRecord{31da7b1e com.here.odnp.service/.LocationService:system}
    conn=android.app.LoadedApk$ServiceDispatcher$InnerConnection@3c782b4a flags=0x40000005
  * ConnectionRecord{10ac71 u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3d006a18}
    binding=AppBindRecord{2062b6c2 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.amazon.media.session.monitor}
    conn=android.os.BinderProxy@3d006a18 flags=0x11
  * ConnectionRecord{129deeee u0 CR OOM com.amazon.client.metrics/.AndroidMetricsServiceAdapter:@3d196469}
    binding=AppBindRecord{2432b255 com.amazon.client.metrics/.AndroidMetricsServiceAdapter:com.android.systemui}
    conn=android.os.BinderProxy@3d196469 flags=0x11
