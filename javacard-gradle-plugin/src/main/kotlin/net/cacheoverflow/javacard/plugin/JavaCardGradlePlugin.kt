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

package net.cacheoverflow.javacard.plugin

import net.cacheoverflow.javacard.javacard_gradle_plugin.BuildConfig
import net.cacheoverflow.javacard.plugin.dsl.JavaCardExtension
import net.cacheoverflow.javacard.plugin.task.gp.GlobalPlatformDeleteTask
import net.cacheoverflow.javacard.plugin.task.gp.GlobalPlatformDownloadTask
import net.cacheoverflow.javacard.plugin.task.gp.GlobalPlatformInstallTask
import net.cacheoverflow.javacard.plugin.task.sdk.JavaCardCompileAppletTask
import net.cacheoverflow.javacard.plugin.task.sdk.JavaCardCreateConfigTask
import net.cacheoverflow.javacard.plugin.util.create
import net.cacheoverflow.javacard.plugin.util.register
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.extensions.core.serviceOf
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

/**
 * @author Cedric Hammes
 * @since  04/07/2026
 */
abstract class JavaCardGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.pluginManager.hasPlugin("java")) {
            throw GradleException("'${BuildConfig.PLUGIN_DISPLAY_NAME}' requires the 'java' plugin to be applied.")
        }

        val toolchainService = project.serviceOf<JavaToolchainService>()
        val mainSourceSet = project.extensions.getByType(SourceSetContainer::class.java).named("main")
        val extension = project.extensions.create<JavaCardExtension>(EXT_NAME).also { extension ->
            extension.toolLauncher.convention(toolchainService.launcherFor {
                it.languageVersion.set(JavaLanguageVersion.of(17))
            })
            extension.javaCompiler.convention(toolchainService.compilerFor {
                it.languageVersion.set(JavaLanguageVersion.of(17))
            })
        }

        // Define the tasks.
        val appletOutputFolder = project.layout.buildDirectory.dir("applet-output")
        val appletConfigFile = project.layout.buildDirectory.file("applet-config.conf")
        val createAppletConfigTask = project.tasks.register<JavaCardCreateConfigTask>("createAppletConfig") { spec ->
            spec.group = TASK_GROUP_ID
            spec.description = "Build the config for the Java Card SDK converter"

            spec.dependsOn(project.tasks.withType(JavaCompile::class.java))
            spec.classesFolder.convention(mainSourceSet.map { sourceSet -> sourceSet.java.classesDirectory.get() })
            spec.outputFolder.convention(appletOutputFolder.map { it.asFile.relativeTo(project.projectDir).path })
            spec.configFile.convention(appletConfigFile)
        }

        @Suppress("UnstableApiUsage")
        val compileAppletTask = project.tasks.register<JavaCardCompileAppletTask>("compileApplet") { spec ->
            spec.group = TASK_GROUP_ID
            spec.description = "Convert the compiled Java source code into an applet"

            spec.dependsOn(createAppletConfigTask, project.tasks.withType(JavaCompile::class.java))
            spec.inputFiles.convention(mainSourceSet.map { sources -> sources.allSource.sourceDirectories })
            spec.appletConfigFile.convention(appletConfigFile)
            spec.outputFolder.convention(appletOutputFolder)
        }

        val gpDownloadTask = project.tasks.register<GlobalPlatformDownloadTask>("downloadGlobalPlatform") { spec ->
            spec.group = TASK_GROUP_ID
            spec.description = "Download the GlobalPlatform tooling required for future tooling use"

            spec.outputFile.convention(extension.globalPlatform.executableFile)
        }

        val gpInstallTask = project.tasks.register<GlobalPlatformInstallTask>("installApplet") { spec ->
            spec.group = TASK_GROUP_ID
            spec.description = "Install the applet using GlobalPlatform to the card"

            spec.dependsOn(gpDownloadTask, compileAppletTask)
            spec.applets.convention(extension.applets)
            spec.appletId.convention(extension.appletId)
            spec.toolFile.convention(extension.globalPlatform.executableFile)
            spec.appletFile.convention(appletOutputFolder.zip(extension.namespace) { outputFolder, namespace ->
                outputFolder.dir(namespace.replace(".", "/")).dir("javacard").file("${namespace.split(".").last()}.cap")
            })
        }

        val gpDeleteTask = project.tasks.register<GlobalPlatformDeleteTask>("deleteApplet") { spec ->
            spec.group = TASK_GROUP_ID
            spec.description = "Delete the applet using GlobalPlatform from the card if installed"

            spec.dependsOn(gpDownloadTask)
            spec.applets.convention(extension.applets)
            spec.appletId.convention(extension.appletId)
            spec.toolFile.convention(extension.globalPlatform.executableFile)
            spec.appletFile.convention(appletOutputFolder.zip(extension.namespace) { outputFolder, namespace ->
                outputFolder.dir(namespace.replace(".", "/")).dir("javacard").file("${namespace.split(".").last()}.cap")
            })
        }

        project.afterEvaluate {
            project.tasks.named(mainSourceSet.get().compileJavaTaskName, JavaCompile::class.java) { compileTask ->
                compileTask.javaCompiler.set(extension.javaCompiler)
                compileTask.options.compilerArgs.add("-Xlint:-options")
                compileTask.sourceCompatibility = "1.7"
                compileTask.targetCompatibility = "1.7"
            }
        }
    }

    companion object {
        const val EXT_NAME: String = "javaCard"
        const val TASK_GROUP_ID = "javacard applet"
    }
}
