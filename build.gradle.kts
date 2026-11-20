plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.kannanjmc.secureview"
version = "1.0.6"

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2025.3")
        pluginVerifier()
        zipSigner()
        bundledPlugin("org.jetbrains.kotlin")
    }
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
}

tasks.test { useJUnitPlatform() }

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = "*"
        }
        changeNotes = "1.0.6: Added reveal action and fixed masking with MarkupModel"
    }

    publishing {
        token.set(providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN"))
    }
}
