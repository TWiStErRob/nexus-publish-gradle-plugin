/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.gradlenexus.publishplugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import java.net.URI
import javax.inject.Inject

@Suppress("UnstableApiUsage")
abstract class NexusRepository @Inject constructor(@Input val name: String, project: Project) {

    @get:Input
    abstract val nexusUrl: Property<URI>

    @get:Input
    abstract val snapshotRepositoryUrl: Property<URI>

    @get:Internal
    abstract val username: Property<String>

    @get:Internal
    abstract val password: Property<String>

    @get:Internal
    abstract val allowInsecureProtocol: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val stagingProfileId: Property<String>

    @get:Internal
    internal val capitalizedName: String by lazy { name.capitalize() }

    init {
        username.set(project.providers.findProperty("${name}Username"))
        password.set(project.providers.findProperty("${name}Password"))
    }
}

/**
 * @see Project.findProperty as it was used to find this, but it's impractical do the same in the new lazy API.
 */
private fun ProviderFactory.findProperty(propertyName: String): Provider<String> =
    gradleProperty(propertyName).orElse(systemProperty(propertyName))
