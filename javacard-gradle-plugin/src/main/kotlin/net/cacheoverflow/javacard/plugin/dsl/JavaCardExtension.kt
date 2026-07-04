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

package net.cacheoverflow.javacard.plugin.dsl

import net.cacheoverflow.javacard.plugin.util.JavaCardVersion
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaLauncher
import javax.inject.Inject

/**
 * @author Cedric Hammes
 * @since  04/07/2026
 */
abstract class JavaCardExtension @Inject constructor(project: Project, objects: ObjectFactory) {
    val globalPlatform: JavaCardGlobalPlatform = objects.newInstance(JavaCardGlobalPlatform::class.java)

    abstract val applets: NamedDomainObjectContainer<JavaCardApplet>
    abstract val cardVersion: Property<JavaCardVersion>
    abstract val toolLauncher: Property<JavaLauncher>
    abstract val javaCompiler: Property<JavaCompiler>
    abstract val sdkFolder: DirectoryProperty
    abstract val namespace: Property<String>
    abstract val appletId: Property<String>

    init {
        this.cardVersion.convention(JavaCardVersion.VERSION_320).finalizeValueOnRead()
        this.namespace.convention(project.group.toString()).finalizeValueOnRead()
        this.toolLauncher.finalizeValueOnRead()
        this.javaCompiler.finalizeValueOnRead()
        this.sdkFolder.finalizeValueOnRead()
        this.appletId.finalizeValueOnRead()
    }

    fun globalPlatform(action: Action<JavaCardGlobalPlatform>) {
        action.execute(globalPlatform)
    }
}
