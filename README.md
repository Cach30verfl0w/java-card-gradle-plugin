

<div align="center">  

# `🚀 JavaCard Gradle Plugin`
**A Gradle plugin allowing JavaCard development with integration for JavaCard SDKs and GlobalPlatformPro**

![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)

</div>

## 🛠 Features
This plugin simplifies the lifecycle of a JavaCard applet by automating the most repetitive tasks:
- **JavaCard SDK Integration:** Provide tasks for the compilation of your project to a JavaCard program
- **GlobalPlatformPro Tasks:** Deploy, install and manage applets on physical smart cards directly via Gradle

## 🚀 Example Usage
Once the plugin is applied, you can define your card configuration and execute lifecycle tasks directly from your terminal:
```kotlin
// build.gradle.kts
plugins {
    id("java")
    id("net.cacheoverflow.gradle.javacard").version("1.0.0")

    javaCard {
        cardVersion.set(JavaCardVersion.VERSION_304)
        namespace.set("net.cacheoverflow.example")
        appletId.set("0x01:0x02:0x03:0x04:0x05:0x06")
        sdkFolder.set(layout.projectDirectory.dir("sdk_path"))

        applets {
            create("TestApplet") {
                appletId.set(0x00)
            }
        }
    }
}
```

## 📄 License

This project is licensed under the Apache License, Version 2.0. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

This plugin relies on the following third-party projects:

| Project Name                                                                     | Author                                           | License                                                                               |
|----------------------------------------------------------------------------------|--------------------------------------------------|---------------------------------------------------------------------------------------|
| [GlobalPlatformPro](https://github.com/martinpaljak/GlobalPlatformPro)           | [Martin Paljak](https://github.com/martinpaljak) | [LGPL 3.0](https://github.com/martinpaljak/GlobalPlatformPro/blob/next/LICENSE)       |
| [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin) | [Guillermo Mozzola](https://github.com/gmazzo)   | [MIT License](https://github.com/gmazzo/gradle-buildconfig-plugin/blob/main/LICENSE)  |