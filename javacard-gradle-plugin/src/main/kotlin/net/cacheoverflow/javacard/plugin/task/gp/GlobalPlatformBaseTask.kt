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

import net.cacheoverflow.javacard.plugin.dsl.JavaCardExtension
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.process.ExecOperations
import org.gradle.process.JavaExecSpec
import javax.inject.Inject

/**
 * @author Cedric Hammes
 * @since  05/07/2026
 */
@CacheableTask
abstract class GlobalPlatformBaseTask : DefaultTask() {

    @get:Inject
    abstract val exec: ExecOperations

    @get:Nested
    abstract val toolLauncher: Property<JavaLauncher>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val toolFile: RegularFileProperty

    init {
        val extension = project.extensions.getByType(JavaCardExtension::class.java)
        toolLauncher.convention(extension.toolLauncher)
    }

    protected fun runTool(action: Action<JavaExecSpec>) {
        val result = exec.javaexec { javaExecSpec ->
            javaExecSpec.mainClass.set("pro.javacard.gptool.GPTool")

            javaExecSpec.executable = toolLauncher.get().executablePath.asFile.absolutePath
            javaExecSpec.classpath = project.files(toolFile.get())
            javaExecSpec.standardOutput = System.out
            javaExecSpec.errorOutput = System.err
            action.execute(javaExecSpec)
        }

        if (result.exitValue != 0) {
            throw GradleException("Failed to run JavaCard SDK tool!");
        }
    }

}
