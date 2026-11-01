package com.kannanjmc.secureview

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener

/** Stateless extension adapter; editor state is owned by the application service. */
class SecureViewEditorFactoryListener : EditorFactoryListener {
    override fun editorCreated(event: EditorFactoryEvent) {
        ApplicationManager.getApplication().getService(EditorMaskingService::class.java)
            .editorCreated(event.editor)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        ApplicationManager.getApplication().getService(EditorMaskingService::class.java)
            .editorReleased(event.editor)
    }
}
