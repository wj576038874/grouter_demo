plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies{
    api("com.google.auto.service:auto-service:1.0-rc2")
    api("com.squareup:javapoet:1.11.1")
    api("com.alibaba:fastjson:1.1.45.android")
    api(project(":grouter-annotation"))

}
