package com.kannanjmc.secureview

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.markup.CustomHighlighterOrder
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics
import java.util.IdentityHashMap

@Service(Service.Level.APP)
class EditorMaskingService : com.intellij.openapi.Disposable {
    private val LOG = Logger.getInstance(EditorMaskingService::class.java)

    private data class EditorState(
        val listener: DocumentListener,
        val regions: MutableList<SecretRange> = mutableListOf(),
        val revealedRanges: MutableSet<SecretRange> = mutableSetOf(),
        val highlighters: MutableList<RangeHighlighter> = mutableListOf()
    )

    private val states = IdentityHashMap<Editor, EditorState>()

    // Transparent text attributes to hide the secret
    private val hiddenAttributes = TextAttributes().apply {
        foregroundColor = Color(0, 0, 0, 0) // Fully transparent
        backgroundColor = Color(0, 0, 0, 0)
    }

    // Mask renderer that draws ******** over the hidden text
    private class MaskRenderer : CustomHighlighterRenderer {
        override fun paint(editor: Editor, highlighter: RangeHighlighter, g: Graphics) {
            val startOffset = highlighter.startOffset
            val endOffset = highlighter.endOffset
            val document = editor.document
            val text = document.immutableCharSequence.toString()
            val secretText = text.substring(startOffset, endOffset)

            // Calculate the visual position of the secret
            val startPoint = editor.offsetToXY(startOffset)
            val endPoint = editor.offsetToXY(endOffset)

            // Draw mask characters
            g.font = editor.contentComponent.font
            g.color = JBColor.GRAY

            // Draw asterisks for each character in the secret
            for (i in secretText.indices) {
                val x = startPoint.x + i * 8 // Approximate character width
                val y = startPoint.y + editor.lineHeight
                g.drawString("*", x, y - 4)
            }
        }

        override fun isForeground(): Boolean = true
        override fun getOrder(): CustomHighlighterOrder = CustomHighlighterOrder.AFTER_TEXT
    }

    fun editorCreated(editor: Editor) {
        if (editor.isDisposed || FileDocumentManager.getInstance().getFile(editor.document) == null) return
        if (states.containsKey(editor)) return

        val state = EditorState(
            object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    // Debounce refresh to avoid masking during typing
                    ApplicationManager.getApplication().invokeLater({
                        if (!editor.isDisposed) {
                            refresh(editor)
                        }
                    }, ModalityState.any())
                }
            }
        )
        states[editor] = state
        editor.document.addDocumentListener(state.listener)

        // Apply masking after editor is fully initialized
        ApplicationManager.getApplication().invokeLater({
            if (!editor.isDisposed) {
                refresh(editor)
            }
        }, ModalityState.any())
    }

    fun editorReleased(editor: Editor) {
        val state = states.remove(editor) ?: return
        editor.document.removeDocumentListener(state.listener)
        state.regions.clear()
        state.revealedRanges.clear()
        state.highlighters.clear()
    }

    private fun refresh(editor: Editor) {
        val state = states[editor] ?: return
        if (editor.isDisposed) return

        val document = editor.document
        val text = document.immutableCharSequence.toString()
        val ranges = PasswordDetector.findSecrets(text)
            .filter { it.start >= 0 && it.end <= document.textLength && it.end > it.start }

        LOG.info("SecureView: Detected ${ranges.size} secrets in document")
        ranges.forEachIndexed { index, range ->
            val secretText = text.substring(range.start, range.end)
            LOG.info("SecureView: Secret $index: range=[$range.start, $range.end), text='$secretText'")
        }

        val markupModel = editor.markupModel

        // Remove existing highlighters
        state.highlighters.forEach { markupModel.removeHighlighter(it) }
        state.highlighters.clear()

        // Add highlighters for secrets
        ranges.forEachIndexed { index, range ->
            try {
                // Check if this range was revealed before
                val wasRevealed = state.revealedRanges.contains(range)

                if (!wasRevealed) {
                    // Add highlighter with transparent text and custom renderer
                    val highlighter = markupModel.addRangeHighlighter(
                        range.start,
                        range.end,
                        HighlighterLayer.LAST + 1,
                        hiddenAttributes,
                        HighlighterTargetArea.EXACT_RANGE
                    )
                    if (highlighter != null) {
                        highlighter.customRenderer = MaskRenderer()
                        state.highlighters += highlighter
                        state.regions += range
                        LOG.info("SecureView: Masked secret $index: range=[$range.start, $range.end)")
                    }
                } else {
                    LOG.info("SecureView: Secret $index was revealed, skipping mask")
                }
            } catch (e: Exception) {
                LOG.error("SecureView: Exception masking secret $index: range=[$range.start, $range.end)", e)
            }
        }

        LOG.info("SecureView: Total secrets masked: ${state.regions.size}")
    }

    fun revealSecret(editor: Editor, offset: Int) {
        val state = states[editor] ?: return
        val document = editor.document
        val text = document.immutableCharSequence.toString()

        // Find the secret at the given offset
        val ranges = PasswordDetector.findSecrets(text)
            .filter { it.start >= 0 && it.end <= document.textLength && it.end > it.start }

        ranges.forEach { range ->
            if (offset >= range.start && offset <= range.end) {
                state.revealedRanges.add(range)
                LOG.info("SecureView: Revealed secret at range=[$range.start, $range.end)")
                refresh(editor)
            }
        }
    }

    override fun dispose() {
        states.keys.toList().forEach(::editorReleased)
    }
}
