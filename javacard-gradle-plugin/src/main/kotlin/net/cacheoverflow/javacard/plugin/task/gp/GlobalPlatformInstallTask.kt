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

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * @author Cedric Hammes
 * @since  05/07/2026
 */
@CacheableTask
abstract class GlobalPlatformInstallTask : GlobalPlatformBaseTask() {

    @get:Input
    abstract val cardKey: Property<String>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val appletFile: RegularFileProperty

    init {
        cardKey.convention(DEFAULT_CARD_KEY).finalizeValueOnRead()
    }

    @TaskAction
    fun executeTask() = runTool { spec ->
        // TODO: Add debug mode
        spec.args = listOf("-key", cardKey.get(), "-install", appletFile.get().asFile.absolutePath)
    }

    companion object {
        const val DEFAULT_CARD_KEY: String = "404142434445464748494A4B4C4D4E4F"
    }

}
