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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.security.DigestInputStream
import java.security.MessageDigest

/**
 * @author Cedric Hammes
 * @since  05/07/2026
 */
@CacheableTask
abstract class GlobalPlatformDownloadTask : DefaultTask() {

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val checksum: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        version.convention(DEFAULT_GP_VERSION).finalizeValueOnRead()
        checksum.convention(DEFAULT_GP_CHECKSUM).finalizeValueOnRead()
    }

    @TaskAction
    fun executeTask() {
        val parts = checksum.get().split(":")
        if (parts.size != 2) {
            throw IllegalArgumentException("Checksum is expected to be in format 'algorithm:checksum'")
        }

        val checksumValue = parts[1]
        val checksumAlgorithm = parts[0]
        val outputFile = outputFile.get().asFile
        URI("${DOWNLOAD_MIRROR_BASE_URL}/v${version.get()}/gp.jar").toURL().openStream().use { inputStream ->
            val digest = MessageDigest.getInstance(checksumAlgorithm)
            DigestInputStream(inputStream, digest).use { digestInputStream ->
                outputFile.outputStream().use { outputStream ->
                    digestInputStream.copyTo(outputStream)
                }
            }

            val expectedHash = checksumValue.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            if (!MessageDigest.isEqual(digest.digest(), expectedHash)) {
                if (outputFile.exists())
                    outputFile.delete()
                throw GradleException("The checksum is invalid! The file is corrupt or has been tempered!")
            }
        }
    }

    companion object {
        const val DOWNLOAD_MIRROR_BASE_URL: String = "https://github.com/martinpaljak/GlobalPlatformPro/releases/download"
        const val DEFAULT_GP_CHECKSUM: String = "sha256:c88e0c5093032ec4571571f5397b6174e56bf632667950fa5bb716338534b122"
        const val DEFAULT_GP_VERSION: String = "25.10.20"
    }
}
