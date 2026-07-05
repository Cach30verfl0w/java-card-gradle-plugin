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

package net.cacheoverflow.javacard.plugin.task.gp

import net.cacheoverflow.javacard.plugin.dsl.JavaCardApplet
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * @author Cedric Hammes
 * @since  05/07/2026
 *
 * @see <a href="https://github.com/martinpaljak/GlobalPlatformPro">GlobalPlatformPro, GitHub</a>
 */
@CacheableTask
abstract class GlobalPlatformInstallTask : GlobalPlatformBaseTask() {

    @get:Input
    abstract val cardKey: Property<String>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val appletFile: RegularFileProperty

    @get:Input
    abstract val applets: SetProperty<JavaCardApplet>

    @get:Input
    abstract val appletId: Property<String>

    init {
        cardKey.convention(DEFAULT_CARD_KEY).finalizeValueOnRead()
    }

    @TaskAction
    fun executeTask() {
        runTool { spec ->
            spec.args = listOf(
                "-key", cardKey.get(),
                "-load", appletFile.get().asFile.absolutePath,
            )
        }

        for (applet in applets.get().toSortedSet(compareBy { applet -> applet.appletId.get() })) {
            val appletId = "${appletId.get()}:${String.format("0x%02X", applet.appletId.get())}"
            logger.lifecycle("Loading applet '${applet.name}' with ID '$appletId'")
            runTool { spec ->
                spec.args = listOf(
                    "-key", cardKey.get(),
                    "-package", this.appletId.get().replace("0x", "").replace(":", ""),
                    "-applet", appletId.replace("0x", "").replace(":", ""),
                    "-create", appletId.replace("0x", "").replace(":", "")
                )
            }
        }
    }

    companion object {
        const val DEFAULT_CARD_KEY: String = "404142434445464748494A4B4C4D4E4F"
    }

}
