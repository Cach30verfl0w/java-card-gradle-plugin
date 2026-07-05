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

package net.cacheoverflow.javacard.plugin.task.simulator

import net.cacheoverflow.javacard.plugin.task.AbstractDownloadTask
import java.net.URI

/**
 * @author Cedric Hammes
 * @since  05/07/2026
 */
abstract class JavaCardSimulatorDownloadTask : AbstractDownloadTask() {
    init {
        version.convention(DEFAULT_VERSION).finalizeValueOnRead()
        checksum.convention(DEFAULT_CHECKSUM).finalizeValueOnRead()
    }

    override fun buildUrl(version: String): URI = URI("$DOWNLOAD_MIRROR_BASE_URL/v$version/jcard.jar")

    companion object {
        const val DOWNLOAD_MIRROR_BASE_URL: String = "https://github.com/martinpaljak/JCardEngine/releases/download"
        const val DEFAULT_CHECKSUM: String = "sha256:16a3d4a3c316b9f24c0f68f97d353ef52b4bdc5a59f1f2c00679ad4aa68ce63a"
        const val DEFAULT_VERSION: String = "26.06.29"
    }
}
