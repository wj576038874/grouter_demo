import com.alibaba.fastjson.JSON
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.grouter.compiler.RouterBuildHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension
import org.gradle.kotlin.dsl.configure
import router.ParserHelper
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Locale
import kotlin.text.capitalize


/**
 * Created by wenjie on 2025/07/15.
 */
class GRouterPlugin : Plugin<Project> {

    var GROUTER_SCHEME: String? = "router"
    var GROUTER_HOST: String? = ""
    var GROUTER_SOURCE_PATH: String? = ""

    var GROUTER_MULTI_PROJECT_MODE = false
    var GROUTER_MULTI_PROJECT_NAME: String? = ""

    var DEV_NOT_AUTO_DEPENDENCIES: Boolean? = false

    override fun apply(project: Project) {
        println("###MyPluginMyPluginMyPlugin")

        val ext = project.rootProject.property("ext") as DefaultExtraPropertiesExtension
        println(ext)
        val OBJECT_GROUTER_MULTI_PROJECT_MODE = ext.properties["GROUTER_MULTI_PROJECT_MODE"]
        GROUTER_MULTI_PROJECT_NAME = ext.properties["GROUTER_MULTI_PROJECT_NAME"] as? String
        GROUTER_SCHEME = ext.properties["GROUTER_SCHEME"] as? String
        GROUTER_HOST = ext.properties["GROUTER_HOST"] as? String
        GROUTER_SOURCE_PATH = ext.properties["GROUTER_SOURCE_PATH"] as String?
        println("#####$GROUTER_SOURCE_PATH")
        DEV_NOT_AUTO_DEPENDENCIES = ext.properties["DEV_NOT_AUTO_DEPENDENCIES"] as? Boolean
        if (GROUTER_MULTI_PROJECT_NAME == null) {
            GROUTER_MULTI_PROJECT_MODE = false
        }
        if (OBJECT_GROUTER_MULTI_PROJECT_MODE != null) {
            GROUTER_MULTI_PROJECT_MODE = OBJECT_GROUTER_MULTI_PROJECT_MODE as Boolean
        }
        if (GROUTER_SOURCE_PATH == null || GROUTER_SOURCE_PATH.isNullOrEmpty()) {
            throw RuntimeException("please in gradle.properties add GROUTER_SOURCE_PATH, e.g: GROUTER_SOURCE_PATH=app/src/main/java")
        }
        if (!GROUTER_MULTI_PROJECT_MODE) {
            GROUTER_MULTI_PROJECT_NAME = ""
        }
        if (GROUTER_HOST == null) {
            GROUTER_HOST = ""
        }
        if (GROUTER_SCHEME == null) {
            GROUTER_SCHEME = "grouter"
        }
        if (GROUTER_MULTI_PROJECT_NAME == null) {
            GROUTER_MULTI_PROJECT_NAME = ""
        }

        val hasApp = project.pluginManager.hasPlugin("com.android.application")
        val hasLib = project.pluginManager.hasPlugin("com.android.library")

        if (!hasApp && !hasLib) {
            throw IllegalStateException("'android' or 'android-library' plugin required.")
        }

        if (hasApp) {
            project.extensions.configure<BaseAppModuleExtension> {
                applicationVariants.configureEach {
                    config(project, this)
                }
                configHtml(project)
            }

        } else {
            project.extensions.configure<LibraryExtension> {
                libraryVariants.configureEach {
                    config(project, this)
                }
                configHtml(project)
            }
        }
    }


    private fun config(project: Project, variant: BaseVariant) {
        val variantName = variant.name.capitalize(Locale.ROOT)
        val sourceDir = File(project.rootDir, GROUTER_SOURCE_PATH)
        val jsonDir = File(sourceDir, "com/grouter/data")
        val arguments = variant.javaCompileOptions.annotationProcessorOptions.arguments
        if (GROUTER_MULTI_PROJECT_NAME != null && !GROUTER_MULTI_PROJECT_NAME.isNullOrEmpty()) {
            arguments.put("GROUTER_MULTI_PROJECT_NAME", GROUTER_MULTI_PROJECT_NAME)
        }
        if (GROUTER_SCHEME != null && !GROUTER_SCHEME.isNullOrEmpty()) {
            arguments.put("GROUTER_SCHEME", GROUTER_SCHEME)
        }
        arguments.put("GROUTER_SOURCE_PATH", sourceDir.getAbsolutePath())
        arguments.put("MODULE_NAME", project.name)
        arguments.put("ROOT_PROJECT_DIR", project.rootDir.absolutePath)
        val task = project.tasks.create("GRouterProcessor$variantName").doLast {

        }
        task.dependsOn(project.tasks.getByName("compile${variantName}JavaWithJavac"))
        task.group = "grouter"
        val routerFixTask = project.tasks.create("GRouterFix$variantName").doLast {
            var projectName = "G"
            if (GROUTER_MULTI_PROJECT_NAME != null && !GROUTER_MULTI_PROJECT_NAME.isNullOrEmpty()) {
                projectName = GROUTER_MULTI_PROJECT_NAME!!
            }
            println("GRouter fix start")
            val startTime = System.currentTimeMillis()
            val files = mutableListOf<File>()
            for (source in variant.sourceSets) {
                files.addAll(source.javaDirectories)
            }
            if (!jsonDir.exists()) {
                jsonDir.mkdirs()
            }
            ParserHelper.parse(jsonDir, files, project.name)
            RouterBuildHelper.build(
                sourceDir, jsonDir, projectName, GROUTER_SCHEME, GROUTER_HOST
            )
            println("GRouter fix end, duration: ${(System.currentTimeMillis() - startTime) / 1000} sec.")
        }
        routerFixTask.group = "grouter"
        routerFixTask.dependsOn(project.tasks.getByName("generate${variantName}Sources"))
    }

    private fun configHtml(project: Project) {
        val exportHTML = project.tasks.create("exportHTML").doFirst {
            val url = this.javaClass.getResource("/META-INF/grouter.html")
            if (url != null) {
                try {
                    val sourceDir = File(project.rootDir, GROUTER_SOURCE_PATH)
                    val jsonDir = File(sourceDir, "com/grouter/data")
                    val routerModel = RouterBuildHelper.getRouterModel(
                        jsonDir, project.name, GROUTER_SCHEME, GROUTER_HOST, true
                    )
                    val activityModels = JSON.toJSONString(routerModel.activityModels, true)
                    val componentModels = JSON.toJSONString(routerModel.componentModels, true)
                    val taskModels = JSON.toJSONString(routerModel.taskModels, true)
                    val delegateModels = JSON.toJSONString(routerModel.delegateModels, true)
                    val isis = this.javaClass.getResourceAsStream("/META-INF/grouter.html")
                    val reader = BufferedReader(InputStreamReader(isis))
                    val lines = reader.readLines()
                    val stringBuilder = StringBuilder()
                    for (line in lines) {
                        stringBuilder.append(line).append('\n')
//                        println(line)
                    }
                    var htmlText = stringBuilder.toString()
                    htmlText = htmlText.replace("@ACTIVITY_MODELS", activityModels)
                    htmlText = htmlText.replace("@COMPONENT_MODELS", componentModels)
                    htmlText = htmlText.replace("@TASK_MODELS", taskModels)
                    htmlText = htmlText.replace("@DELEGATE_MODELS", delegateModels)
                    val outFile = File(project.rootDir, "build/grouter.html")
                    outFile.writeText(htmlText)
                    println("生成HTML：file://" + outFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                println("找不到")
            }
        }
        exportHTML.group = "GRouter"
        val kotlinMode = project.plugins.hasPlugin("kotlin-android")
        // 如果是Kotlin项目，应该判断是否有 kotlin-kapt，如果没有就添加
        if (kotlinMode && !project.plugins.hasPlugin("kotlin-kapt")) {
            project.plugins.apply("kotlin-kapt")
            println("GRouter add project.plugins.apply(\"kotlin-kapt\")")
        }
        if (DEV_NOT_AUTO_DEPENDENCIES != null && DEV_NOT_AUTO_DEPENDENCIES.toString()
                .toBoolean()
        ) {
            println("开发模式，不自动增加依赖")
            return
        }
//        val version = "1.2.1"
//        project.dependencies {
//            add("api", "com.grouter:grouter:$version")
//            if (kotlinMode) {
//                // 增加构造器
//                add("kapt", "com.grouter:grouter-compiler:$version")
////                project.println("GRouter add kapt 'com.grouter:grouter-compiler:$version'")
//            } else {
//                // Java 项目
//                add("annotationProcessor", "com.grouter:grouter-compiler:$version")
////                project.println("GRouter add annotationProcessor 'com.grouter:grouter-compiler:$version'")
//            }
//        }
    }
}