package com.kannanjmc.secureview

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
            token=XYZ987654
        """.trimIndent()

        val values = PasswordDetector.findSecrets(input).map { input.substring(it.start, it.end) }
        assertEquals(listOf("DbSecret123", "YamlSecret", "JsonSecret", "JavaSecret", "XYZ987654"), values)
    }

    @Test
    fun detectsXmlElementText() {
        val input = "<password>XmlSecret</password>"
        val ranges = PasswordDetector.findSecrets(input)
        assertEquals(listOf("XmlSecret"), ranges.map { input.substring(it.start, it.end) })
    }

    @Test
    fun detectsMultipleSecretsOnFirstLine() {
        val input = "secret=sdfsdfdsfsdfsdf\npassword=MyPassword123\napi_key=ABC123XYZ\ntoken=XYZ987654"
        val ranges = PasswordDetector.findSecrets(input)
        val values = ranges.map { input.substring(it.start, it.end) }
        assertEquals(listOf("sdfsdfdsfsdfsdf", "MyPassword123", "ABC123XYZ", "XYZ987654"), values)
    }

    @Test
    fun detectsSecretsWithBlankLines() {
        val input = "\n\n\nsecret=test123\npassword=pass456"
        val ranges = PasswordDetector.findSecrets(input)
        val values = ranges.map { input.substring(it.start, it.end) }
        assertEquals(listOf("test123", "pass456"), values)
    }
}
