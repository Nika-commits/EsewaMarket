plugins {
    kotlin("plugin.serialization") version "2.4.10"
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") version "4.5.0" apply false
}

android {
    namespace = "com.example.xml_app"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.xml_app"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    implementation("androidx.datastore:datastore:1.2.1")

    implementation("androidx.datastore:datastore-core:1.2.1")
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("androidx.datastore:datastore-preferences-core:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

}