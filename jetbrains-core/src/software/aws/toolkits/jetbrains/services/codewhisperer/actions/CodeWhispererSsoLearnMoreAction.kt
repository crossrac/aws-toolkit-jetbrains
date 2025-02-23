// Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.codewhisperer.actions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import software.aws.toolkits.resources.message
import java.net.URI

class CodeWhispererSsoLearnMoreAction : AnAction(message("aws.settings.learn_more")), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.browse(URI("https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/codewhisperer.html"))
    }
}
