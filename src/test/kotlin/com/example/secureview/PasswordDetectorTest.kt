package com.example.secureview

import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordDetectorTest {
    @Test
    fun detectsPropertiesYamlJsonAndJavaAssignments() {
        val input = """
            spring.datasource.password=DbSecret123
            password: 'YamlSecret'
            { "api_key": "JsonSecret" }
            String password = "JavaSecret";
            username=admin
        """.trimIndent()

        val values = PasswordDetector.findSecrets(input).map { input.substring(it.start, it.end) }
        assertEquals(listOf("DbSecret123", "YamlSecret", "JsonSecret", "JavaSecret"), values)
    }

    @Test
    fun detectsXmlElementText() {
        val input = "<password>XmlSecret</password>"
        val ranges = PasswordDetector.findSecrets(input)
        assertEquals(listOf("XmlSecret"), ranges.map { input.substring(it.start, it.end) })
    }
}
