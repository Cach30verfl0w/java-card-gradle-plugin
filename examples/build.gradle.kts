import net.cacheoverflow.javacard.plugin.util.JavaCardVersion
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/*
 * Copyright 2026 Cedric Hammes <contact@cach30verfl0w.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("java")
    alias(libs.plugins.javaCardGradle)
}

group = "net.cacheoverflow.javacard"
version = "1.0"

dependencies {
    val sdkFolder = Paths.get(layout.projectDirectory.dir("sdks/jc320v26.0_kit").asFile.absolutePath)
    for (file in Files.walk(sdkFolder).collect(Collectors.toList())) {
        compileOnly(files(file.toAbsolutePath().toString()))
    }
}

javaCard {
    cardVersion.set(JavaCardVersion.VERSION_304)
    namespace.set("net.cacheoverflow.example")
    appletId.set("0x01:0x02:0x03:0x04:0x05:0x06")
    sdkFolder.set(layout.projectDirectory.dir("sdks/jc320v26.0_kit"))

    applets {
        create("TestApplet") {
            appletId.set(0x00)
        }
    }

    globalPlatform {

    }
}
