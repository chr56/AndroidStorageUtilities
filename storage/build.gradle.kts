import java.util.Properties

plugins {
    alias(libs.plugins.androidGradlePluginLibrary)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("signing")

    // alias(libs.plugins.kotlin.serialization)
    // alias(libs.plugins.kotlin.parcelize)
    // alias(libs.plugins.kotlin.ksp)
}

val libVersion = "0.1.0"

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 21
        namespace = "lib.stroage"

        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = 21
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        publishing {
            singleVariant("release") {
                withSourcesJar()
                withJavadocJar()
            }
        }
    }

    buildFeatures {
        buildConfig = false
        // viewBinding = true
        // compose = true
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.documentfile)

    implementation(libs.kotlinx.coroutines)

    compileOnly(libs.androidx.core)

//    implementation(libs.kotlinx.serialization)

}

val secretPropsFile = rootProject.file("secrets.properties")
var secrets = Properties()
if (secretPropsFile.exists()) {
    secretPropsFile.inputStream().use {
        secrets.load(it)
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {

            groupId = "io.github.chr56"
            artifactId = "android-storage-utilities"
            version = libVersion

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Android Storage Utilities")
                url.set("https://github.com/chr56/AndroidStorageUtilities")

                licenses {
                    license {
                        name.set("MPL-2.0")
                        url.set("https://www.mozilla.org/MPL/2.0/")
                    }
                }
                developers {
                    developer {
                        id.set("chr_56")
                        name.set("chr_56")
                        timezone.set("UTC+8")
                    }
                }
                scm {
                    connection.set("https://github.com/chr56/AndroidStorageUtilities.git")
                    developerConnection.set("https://github.com/chr56/AndroidStorageUtilities.git")
                    url.set("https://github.com/chr56/AndroidStorageUtilities")
                }
            }
        }
    }
}

if (secretPropsFile.exists()) {
    signing {
        sign(publishing.publications)
        val key = File(secrets["signing_file"] as String).readText()
        useInMemoryPgpKeys(
            secrets["signing_key"] as String,
            key,
            secrets["signing_password"] as String
        )
    }
}