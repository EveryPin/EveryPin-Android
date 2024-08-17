import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import kotlin.math.sign

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "everypin.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "everypin.app"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["kakaoNativeAppKey"] = getLocalPropertyValue("kakao.native.app.key")
        manifestPlaceholders["googleMapApiKey"] = getLocalPropertyValue("google.map.api.key")

        buildConfigField(
            "String",
            "BASE_URL",
            "\"https://everypin-api.azurewebsites.net\""
        )
        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"${getLocalPropertyValue("kakao.native.app.key")}\""
        )
        buildConfigField(
            "String",
            "KAKAO_REST_API_KEY",
            "\"${getLocalPropertyValue("kakao.rest.api.key")}\""
        )
        buildConfigField(
            "String",
            "GOOGLE_SIGN_IN_SERVER_CLIENT_ID",
            "\"${getLocalPropertyValue("google.sign.in.server.client.id")}\""
        )
    }

    signingConfigs {
        create("releaseSigningConfig") {
            storeFile = signingConfigs.getByName("debug").storeFile
            keyAlias = signingConfigs.getByName("debug").keyAlias
            keyPassword = signingConfigs.getByName("debug").keyPassword
            storePassword = signingConfigs.getByName("debug").storePassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("releaseSigningConfig")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

fun getLocalPropertyValue(key: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(key)
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.play.services.location)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashScreen)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    implementation(libs.okHttp)
    implementation(libs.okHttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converterMoshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.kotlinCodegen)

    implementation(libs.coil.compose)

    implementation(libs.kakao.user)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.playServicesAuth)
    implementation(libs.googleId)

    implementation(libs.mapsCompose)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.paging.runtimeKtx)
    implementation(libs.androidx.paging.compose)
}