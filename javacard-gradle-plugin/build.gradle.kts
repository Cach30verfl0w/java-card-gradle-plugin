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
    kotlin("jvm")
    alias(libs.plugins.buildConfig)
    `java-gradle-plugin`
}

group = property("project.group").toString()
version = libs.versions.javaCardGradle.toString()

buildConfig {
    buildConfigField("PLUGIN_DISPLAY_NAME", property("project.plugin.displayName").toString())
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation(kotlin("stdlib"))
}

gradlePlugin {
    plugins {
        create(property("project.plugin.id").toString()) {
            id = property("project.plugin.id").toString()
            implementationClass = "$group.plugin.JavaCardGradlePlugin"
            description = property("project.plugin.description").toString()
            displayName = property("project.plugin.displayName").toString()
            version = version.toString()

            tags.set(listOf("javacard", "jcard", "smartcard", "sdk"))
        }
    }
}
