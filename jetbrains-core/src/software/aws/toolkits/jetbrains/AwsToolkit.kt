// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.registry.Registry

object AwsToolkit {
    const val PLUGIN_ID = "aws.toolkit"
    const val GITHUB_URL = "https://github.com/aws/aws-toolkit-jetbrains"
    const val AWS_DOCS_URL = "https://docs.aws.amazon.com/console/toolkit-for-jetbrains"

    val PLUGIN_VERSION: String by lazy {
        DESCRIPTOR?.version ?: "Unknown"
    }

    val DESCRIPTOR: PluginDescriptor? by lazy {
        PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
    }

    fun pluginPath() = DESCRIPTOR?.pluginPath ?: throw RuntimeException("Toolkit root not available")

    fun isDeveloperMode() = Registry.`is`("aws.toolkit.developerMode", false)
}
