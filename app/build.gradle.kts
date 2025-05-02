plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.lokrey.lqN5M2Xy.mydailyluck"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lokrey.lqN5M2Xy.mydailyluck"
        minSdk = 23
        targetSdk = 35
        versionCode = 26

        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Unterstützt große Bildschirme
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("boolean", "DEBUG", "true")
        }
        release {

            ndk {
                debugSymbolLevel = "FULL"  // Oder "SYMBOL_TABLE" für nur Tabellen
            }
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("boolean", "DEBUG", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }



}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.work.runtime.ktx)
    implementation (libs.ui)
    implementation (libs.material3)
    implementation (libs.integrity)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.material)
    implementation(libs.compose.markdown)
    implementation (libs.core)
    implementation (libs.markwon.linkify)
    implementation (libs.androidx.foundation)
    implementation (libs.androidx.ui.text)
    implementation (libs.accompanist.systemuicontroller)
    implementation (libs.accompanist.systemuicontroller.vletzteversion)





}