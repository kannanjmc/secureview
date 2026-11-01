package com.example.secureview

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Disposer
import java.util.IdentityHashMap

@Service(Service.Level.APP)
class EditorMaskingService : com.intellij.openapi.Disposable {
    private data class EditorState(
        val listener: DocumentListener,
        val regions: MutableList<FoldRegion> = mutableListOf(),
        var refreshQueued: Boolean = false
    )

    private val states = IdentityHashMap<Editor, EditorState>()

    fun editorCreated(editor: Editor) {
        if (editor.isDisposed || FileDocumentManager.getInstance().getFile(editor.document) == null) return
        if (states.containsKey(editor)) return

        val state = EditorState(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                queueRefresh(editor)
            }
        })
        states[editor] = state
        editor.document.addDocumentListener(state.listener)
        refresh(editor)
    }

    fun editorReleased(editor: Editor) {
        val state = states.remove(editor) ?: return
        editor.document.removeDocumentListener(state.listener)
        // The editor is being disposed; its folding model and regions are disposed with it.
        state.regions.clear()
    }

    private fun queueRefresh(editor: Editor) {
        val state = states[editor] ?: return
        if (state.refreshQueued) return
        state.refreshQueued = true
        ApplicationManager.getApplication().invokeLater({
            val current = states[editor] ?: return@invokeLater
            current.refreshQueued = false
            if (!editor.isDisposed) refresh(editor)
        }, ModalityState.any())
    }

    private fun refresh(editor: Editor) {
        val state = states[editor] ?: return
        if (editor.isDisposed) return

        val document = editor.document
        val text = document.immutableCharSequence.toString()
        val ranges = PasswordDetector.findSecrets(text)
            .filter { it.start >= 0 && it.end <= document.textLength && it.end > it.start }

        editor.foldingModel.runBatchFoldingOperation {
            state.regions.toList().forEach { region ->
                if (!region.isExpanded) editor.foldingModel.removeFoldRegion(region)
                else editor.foldingModel.removeFoldRegion(region)
            }
            state.regions.clear()

            ranges.forEach { range ->
                val region = editor.foldingModel.addFoldRegion(range.start, range.end, "********")
                if (region != null) {
                    region.isExpanded = false
                    state.regions += region
                }
            }
        }
    }

    override fun dispose() {
        states.keys.toList().forEach(::editorReleased)
    }
}
