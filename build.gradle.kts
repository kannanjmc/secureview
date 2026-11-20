plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.kannanjmc.secureview"
version = "1.0.9"

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
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
        intellijIdea("2026.2")
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
            sinceBuild = "262"
        }
        changeNotes = "1.0.9: Fixed sinceBuild for IntelliJ 2026.2 compatibility"
    }

    publishing {
        token.set(providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN"))
    }
}
