plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    // Dependency Injection (domain-safe)
    implementation("javax.inject:javax.inject:1")

    // REQUIRED for Kotlin Flow
    implementation(libs.kotlinx.coroutines.core)
}
