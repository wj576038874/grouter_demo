plugins {
    `kotlin-dsl`
    "java"
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
repositories {
    google()
    gradlePluginPortal()
}

sourceSets {
    getByName("main") {
        java.srcDirs("src/main/java")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:8.6.1")
    implementation("org.jetbrains.kotlin:kotlin-compiler-runner:1.3.31")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.31")
    implementation("de.defmacro:eclipse-astparser:8.1")
    implementation("com.squareup:javapoet:1.11.1")
    implementation("com.alibaba:fastjson:1.1.45.android")
//    implementation(project(":grouter-compiler"))
}

gradlePlugin {
    plugins {
        register("GRouterPlugin") {
            id = "grouter"
            implementationClass = "GRouterPlugin"
        }
    }
}
