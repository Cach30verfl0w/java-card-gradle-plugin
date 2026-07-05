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

package net.cacheoverflow.javacard.plugin.task

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
abstract class AbstractDownloadTask : DefaultTask() {

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val checksum: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    abstract fun buildUrl(version: String): URI

    @TaskAction
    fun performDownload() {
        val (algorithm, expectedHex) = checksum.get().split(":")
        val destination = outputFile.get().asFile

        buildUrl(version.get()).toURL().openStream().use { inputStream ->
            val digest = MessageDigest.getInstance(algorithm)
            DigestInputStream(inputStream, digest).use { dis ->
                destination.outputStream().use { os -> dis.copyTo(os) }
            }

            val actualHex = digest.digest().joinToString("") { "%02x".format(it) }
            if (!actualHex.equals(expectedHex, ignoreCase = true)) {
                if (destination.exists()) destination.delete()
                throw GradleException("Checksum mismatch! Expected $expectedHex, got $actualHex")
            }
        }
    }

}
