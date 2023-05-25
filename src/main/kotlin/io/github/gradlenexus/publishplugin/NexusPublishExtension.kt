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

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.container
import org.gradle.kotlin.dsl.newInstance
import java.time.Duration

@Suppress("UnstableApiUsage")
abstract class NexusPublishExtension(project: Project) {

    companion object {
        internal const val NAME = "nexusPublishing"
    }

    abstract val useStaging: Property<Boolean>
    abstract val packageGroup: Property<String>
    abstract val repositoryDescription: Property<String>
    abstract val clientTimeout: Property<Duration>
    abstract val connectTimeout: Property<Duration>
    abstract val transitionCheckOptions: Property<TransitionCheckOptions>

    fun transitionCheckOptions(action: Action<in TransitionCheckOptions>) {
        action.execute(transitionCheckOptions.get())
    }

    val repositories: NexusRepositoryContainer = run {
        // Note: these 2 could be injected in NexusPublishExtension constructor.
        val objects = project.objects
        val providers = project.providers
        project.objects.newInstance(
            DefaultNexusRepositoryContainer::class,
            // `project.container(NexusRepository::class) { name -> ... }`,
            // but in Kotlin 1.3 "New Inference" is not implemented yet, so we have to be explicit.
            // https://kotlinlang.org/docs/whatsnew14.html#new-more-powerful-type-inference-algorithm
            project.container(
                NexusRepository::class,
                NamedDomainObjectFactory { name ->
                    objects.newInstance<NexusRepository>(name, objects, providers)
                }
            )
        )
    }

    fun repositories(action: Action<in NexusRepositoryContainer>) = action.execute(repositories)
}
