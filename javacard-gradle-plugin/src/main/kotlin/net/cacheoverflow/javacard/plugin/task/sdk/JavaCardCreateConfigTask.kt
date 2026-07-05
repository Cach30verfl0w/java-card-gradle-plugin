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

package net.cacheoverflow.javacard.plugin.task.sdk

import net.cacheoverflow.javacard.plugin.dsl.JavaCardApplet
import net.cacheoverflow.javacard.plugin.dsl.JavaCardExtension
import net.cacheoverflow.javacard.plugin.util.JavaCardVersion
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * @author Cedric Hammes
 * @since  04/07/2026
 */
@CacheableTask
abstract class JavaCardCreateConfigTask : DefaultTask() {

    @get:Input
    abstract val appletId: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classesFolder: DirectoryProperty

    @get:Input
    abstract val targetVersion: Property<JavaCardVersion>

    @get:Input
    abstract val applets: SetProperty<JavaCardApplet>

    @get:Input
    abstract val namespace: Property<String>

    @get:Input
    abstract val appVersion: Property<String>

    @get:Input
    abstract val outputFolder: Property<String>

    @get:OutputFile
    abstract val configFile: RegularFileProperty

    init {
        val extension = project.extensions.getByType(JavaCardExtension::class.java)
        appVersion.convention(project.version.toString().toMajorMinorFormat()).finalizeValueOnRead()
        targetVersion.convention(extension.cardVersion).finalizeValueOnRead()
        namespace.convention(extension.namespace).finalizeValueOnRead()
        appletId.convention(extension.appletId).finalizeValueOnRead()
        applets.convention(extension.applets).finalizeValueOnRead()
    }

    @TaskAction
    fun executeTask() {
        val applets = applets.get().toSortedSet(compareBy { applet -> applet.appletId.get() })
        val appletConfigurations = applets.map { it.createAppletConfig() }.toTypedArray()
        val configLines = listOf(
            "-nobanner",
            *appletConfigurations,
            "-out CAP JCA EXP",
            "-d ${outputFolder.get()}",
            "-classdir ${classesFolder.get()}",
            "-target ${targetVersion.get()}",
            namespace.get(),
            "${appletId.get()} ${appVersion.get()}"
        )

        val file = configFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(configLines.joinToString("\n"))
        logger.lifecycle("Wrote configuration for Applets")
    }

    private fun JavaCardApplet.createAppletConfig(): String {
        val appletId = "${this@JavaCardCreateConfigTask.appletId.get()}:${String.format("0x%02X", appletId.get())}"
        val className = "${namespace.get()}.${name}"
        logger.lifecycle("Creating a new applet with ID '$appletId' for class '$className'")
        return "-applet $appletId $className"
    }

    private fun String.toMajorMinorFormat(): String {
        val regex = """^(\d+)\.(\d+)""".toRegex()
        val match = regex.find(this)

        return match?.let { "${it.groupValues[1]}.${it.groupValues[2]}" }
            ?: throw IllegalArgumentException("Version '$this' is not matching the 'major.minor' version format.")
    }

}
