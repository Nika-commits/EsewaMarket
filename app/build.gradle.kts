plugins {
    kotlin("plugin.serialization") version "2.4.10"

    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
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

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }

            buildConfigField(
                "String",
                "MapboxAccessToken",
                providers.gradleProperty("MAPBOX_ACCESS_TOKEN").get()
            )

            buildConfigField(
                "String",
                "EsewaClientId",
                providers.gradleProperty("ESEWA_CLIENT_SECRET").get()
            )

            buildConfigField(
                "String",
                "EsewaClientSecret",
                providers.gradleProperty("ESEWA_CLIENT_SECRET").get()
            )

            buildConfigField(
                "String",
                "EsewaIntentProductCode",
                providers.gradleProperty("ESEWA_INTENT_PRODUCT_CODE").get()
            )

            buildConfigField(
                "String",
                "EsewaClientSecret",
                providers.gradleProperty("ESEWA_INTENT_CLIENT_SECRET").get()
            )
        }
        debug {
            buildConfigField(
                "String",
                "MapboxAccessToken",
                providers.gradleProperty("MAPBOX_ACCESS_TOKEN").get()
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {

    // Navigation
    val navVersion = "2.9.8"

    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")


    // Compose
    val composeBom = platform(libs.androidx.compose.bom)

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3.adaptive:adaptive")

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.compose.runtime:runtime-livedata")


    // Images
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")


    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.datastore:datastore:1.2.1")


    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")


    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.firebaseui:firebase-ui-auth:9.0.0")


    // Credential Manager / Google Sign-In
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")


    // Room 3
    val roomVersion = "3.0.1"

    implementation("androidx.room3:room3-runtime:$roomVersion")
    ksp("androidx.room3:room3-compiler:$roomVersion")


    // Google Maps SDK

//    implementation("com.google.maps.android:maps-compose:8.4.0")

    //Mapbox
    implementation("com.mapbox.maps:android-ndk27:11.28.3")
    implementation("com.mapbox.extension:maps-compose-ndk27:11.28.3")

    //Google Play Services
    implementation("com.google.android.gms:play-services-location:21.4.0")
    // UI

    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation(libs.flexbox)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)


    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)


    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)


    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)

    //esewa
    debugImplementation(files("libs/eSewaPaymentSdk-debug.aar"))
    releaseImplementation(files("libs/eSewaPaymentSdk-release.aar"))
}