import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

sqldelight {
    database("ReluctDatabase") {
        packageName = "work.racka.reluct.common.database.db"
        sourceFolders = listOf("sqldelight")
    }
}

android {
    compileSdk = AppConfig.compileSdkVersion

    defaultConfig {
        minSdk = AppConfig.minSdkVersion
        targetSdk = AppConfig.targetSdkVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:model"))

                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Log.kermit)
                with(Dependencies.Squareup.SQLDelight) {
                    implementation(runtime)
                    implementation(coroutineExtensions)
                }
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(Dependencies.Kotlin.dateTime)
                implementation(Dependencies.Koin.test)
                implementation(Dependencies.Kotlin.Coroutines.test)
                implementation(Dependencies.Squareup.Testing.turbine)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Squareup.SQLDelight.androidDriver)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(Dependencies.Squareup.SQLDelight.sqliteDriver)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(Dependencies.Squareup.SQLDelight.sqliteDriver)
                implementation(Dependencies.Log.slf4j)
            }
        }

        val desktopTest by getting
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}
