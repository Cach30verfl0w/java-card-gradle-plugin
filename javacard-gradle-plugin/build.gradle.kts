import org.gradle.plugin.compatibility.compatibility

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
    `java-gradle-plugin`
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.gradle.compatibility)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.mavenPublish)
    signing
}

val pluginDisplayName: String = property("project.plugin.displayName").toString()
val pluginDescription: String = property("project.plugin.description").toString()
val pluginWebsite: String = property("project.plugin.website").toString()
val pluginId: String = property("project.plugin.id").toString()

group = property("project.group").toString()
version = libs.versions.javaCardGradle.get()

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

signing {
    useGpgCmd()
    isRequired = false
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    println(name)
    coordinates(group.toString(), name, version.toString())

    pom {
        name = pluginDisplayName
        description = pluginDescription
        url = pluginWebsite

        licenses {
            license {
                name = "Apache License, version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "cach30verfl0w"
                name = "Cedric Hammes"
                email = "contact@cach30verfl0w.net"
                url = "https://cach30verfl0w.net"
                timezone = "Europe/Berlin"
            }
        }
        scm {
            val repositoryDomain = property("project.repository.domain")?.toString()
            val repositoryUsername = property("project.repository.username")?.toString()
            val repositoryName = property("project.repository.name")?.toString()
            if (repositoryDomain != null && repositoryUsername != null && repositoryName != null) {
                logger.lifecycle("Configure SCM connection...")
                url = "https://$repositoryDomain/$repositoryUsername/$repositoryName"
                connection = "scm:git:git://$repositoryDomain/$repositoryUsername/$repositoryName.git"
                developerConnection = "scm:git:ssh://git@$repositoryDomain/$repositoryUsername/$repositoryName.git"
            }
        }
    }
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
    }
}

gradlePlugin {
    website = pluginWebsite
    vcsUrl = pluginWebsite

    plugins {
        create(pluginId) {
            id = pluginId
            implementationClass = "$group.plugin.JavaCardGradlePlugin"
            description = pluginDescription
            displayName = pluginDisplayName
            version = version.toString()
            tags.set(listOf("javacard", "jcard", "smartcard", "sdk"))

            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
    }
}
