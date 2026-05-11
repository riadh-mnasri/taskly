plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.assertj.core)
}
