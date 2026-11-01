package com.kannanjmc.secureview

/** A half-open document range containing only the secret value (not its quotes). */
data class SecretRange(val start: Int, val end: Int)

/**
 * Heuristic, format-agnostic detector for common assignment syntaxes used by
 * Java/Kotlin, .properties, YAML, JSON, and XML attributes.
 * It deliberately handles single-line values only.
 */
object PasswordDetector {
    private val sensitiveKey = "(?:password|passwd|pwd|secret|client[_-]?secret|api[_-]?key|access[_-]?token|auth[_-]?token|refresh[_-]?token|private[_-]?key|credential|credentials)"

    private val assignmentPattern = Regex(
        "(?i)(?<![A-Za-z0-9_-])(?:\"|')?$sensitiveKey(?:\"|')?\\s*(?:=|:)\\s*(?:\"([^\"\\r\\n]*)\"|'([^'\\r\\n]*)'|([^,\\s;\\]}]+))"
    )

    private val xmlElementPattern = Regex(
        "(?is)<($sensitiveKey)\\b[^>]*>\\s*([^<\\r\\n]+?)\\s*</\\1\\s*>"
    )

    fun findSecrets(text: String): List<SecretRange> {
        val ranges = mutableListOf<SecretRange>()

        assignmentPattern.findAll(text).forEach { match ->
            val group = (1..3).firstOrNull { match.groups[it] != null } ?: return@forEach
            val value = match.groups[group] ?: return@forEach
            if (value.value.isNotBlank()) ranges += SecretRange(value.range.first, value.range.last + 1)
        }

        xmlElementPattern.findAll(text).forEach { match ->
            val value = match.groups[2] ?: return@forEach
            if (value.value.isNotBlank()) ranges += SecretRange(value.range.first, value.range.last + 1)
        }

        // Deduplicate and discard overlapping ranges to keep FoldingModel happy.
        return ranges.distinctBy { it.start to it.end }
            .sortedBy { it.start }
            .fold(mutableListOf<SecretRange>()) { accepted, next ->
                if (accepted.isEmpty() || next.start >= accepted.last().end) accepted += next
                accepted
            }
    }
}
