# OTG Auto (Oppo / ColorOS)

Earphone / USB lagte hi (ya screen unlock par) Settings me **OTG connection** toggle ko automatically ON karne wala Android app. Root nahi chahiye.

## Kaise kaam karta hai
```
TriggerService (foreground)
  ├─ ACTION_HEADSET_PLUG
  ├─ ACTION_USB_DEVICE_ATTACHED
  └─ ACTION_USER_PRESENT (backup)
          ↓
OtgAccessibilityService
  Settings → Additional settings → OTG connection → toggle ON → Home
```

## Project map
```
OtgAuto/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/otgauto/
        │   ├── MainActivity.kt
        │   ├── TriggerService.kt
        │   ├── OtgAccessibilityService.kt
        │   └── BootReceiver.kt
        └── res/
            ├── values/strings.xml
            └── xml/accessibility_config.xml
```

## Build
1. Android Studio me folder open karo (Gradle wrapper wo khud bana lega).
2. Run karo ya `Build > Build APK`.

## Setup (phone par)
1. App kholo -> "Accessibility ON" -> OTG Auto enable.
2. "Display over other apps" allow.
3. Battery me "Allow background activity".
4. "Service START" dabao.
5. "Test: OTG abhi ON karo" se check karo.

## Known limits
- OTG off ho to phone shayad headset event bheje hi na; tab unlock trigger kaam aata hai.
- ColorOS version me menu ka text alag ho to `NEXT_LABELS` / `OTG_LABELS` badlo.
- Oppo OTG ko ~10 min baad khud off kar deta hai.
