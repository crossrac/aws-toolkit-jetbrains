// Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains.services.codewhisperer.status

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.ui.AnimatedIcon
import com.intellij.util.Consumer
import software.aws.toolkits.jetbrains.services.codewhisperer.service.CodeWhispererInvocationStateChangeListener
import software.aws.toolkits.jetbrains.services.codewhisperer.service.CodeWhispererInvocationStatus
import software.aws.toolkits.resources.message
import java.awt.event.MouseEvent
import javax.swing.Icon

class CodeWhispererStatusBarWidget(project: Project) :
    EditorBasedWidget(project),
    StatusBarWidget.MultipleTextValuesPresentation {

    override fun install(statusBar: StatusBar) {
        super.install(statusBar)
        ApplicationManager.getApplication().messageBus.connect().subscribe(
            CodeWhispererInvocationStatus.CODEWHISPERER_INVOCATION_STATE_CHANGED,
            object : CodeWhispererInvocationStateChangeListener {
                override fun invocationStateChanged(value: Boolean) {
                    statusBar.updateWidget(ID)
                }
            }
        )
    }

    override fun ID(): String = ID

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getTooltipText(): String = message("codewhisperer.statusbar.tooltip")

    override fun getClickConsumer(): Consumer<MouseEvent>? = null

    override fun getPopupStep(): ListPopup? = null

    override fun getSelectedValue(): String = message("codewhisperer.statusbar.display_name")

    override fun getIcon(): Icon =
        if (CodeWhispererInvocationStatus.getInstance().hasExistingInvocation()) {
            AnimatedIcon.Default()
        } else {
            AllIcons.Actions.Commit
        }

    companion object {
        const val ID = "aws.codewhisperer.statusWidget"
    }
}
