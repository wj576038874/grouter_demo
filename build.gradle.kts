// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

buildscript{
    dependencies{
//        classpath(project("grouter-gradle-plugin"))
//        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("com.github.wj576038874:BetaUploader-1.5.2:1.5.3")

    }
}