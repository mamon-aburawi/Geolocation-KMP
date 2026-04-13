import com.android.build.api.dsl.androidLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "io.geolocation.kmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_21
                )
            }
        }
    }

    js{
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {


            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)


            implementation(libs.bundles.ktor)

        }



        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.gms.play.services.location)
            implementation(libs.coroutines.play.services)

            implementation(libs.androidx.activity.ktx)

        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }


        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.cio)
        }

        webMain.dependencies {
            implementation(libs.compass.geolocation)
        }


    }
}


group = "io.github.mamon-aburawi" // this group name in maven central repository
version = "1.0.0" // version of library

mavenPublishing {

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
            androidVariantsToPublish = listOf("release", "debug"),
        )
    )


    coordinates(
        groupId = group.toString(),
        version = version.toString(),
        artifactId = "geoloction-kmp"
    )

    pom {
        name = "GeoLocation KMP"
        description = "A modern, clean, and coroutine-based Kotlin Multiplatform (KMP) library for fetching precise GPS coordinates and resolving them into human-readable street addresses."
        inceptionYear = "2026"
        url = "https://github.com/mamon-aburawi/Geolocation-KMP"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                name = "Mamon Aburawi"
                email = "mamon.aburawi@gmail.com"
            }
        }
        scm {
            url = "https://github.com/mamon-aburawi/Geolocation-KMP"
        }
    }

    publishToMavenCentral()

    signAllPublications()
}