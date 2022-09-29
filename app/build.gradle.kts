plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
    id "com.google.protobuf" version "0.8.17"
}

android {
    namespace = "com.rzmmzdh.toro"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rzmmzdh.toro"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}
dependencies {
    implementation(Dep.coreKtx)
    implementation(Dep.ifecycleRuntimeKtx)
    implementation(Dep.roomKtx)

    implementation(Dep.composeUi)
    implementation(Dep.uiToolingPreview)
    implementation(Dep.activityCompose)
    debugImplementation(Dep.uiTooling)
    debugImplementation(Dep.uiTestManifest)
    implementation(Dep.material3)
    implementation(Dep.navigationCompose)
    implementation(Dep.accompanistSystemUiController)
    implementation(Dep.accompanistNavigationAnimation)

    testImplementation(Dep.junit)
    androidTestImplementation(Dep.junitAndroid)
    androidTestImplementation(Dep.espressoCore)
    androidTestImplementation(Dep.UiTestJunit4)

    implementation(Dep.roomRuntime)
    kapt(Dep.roomCompiler)

    implementation(Dep.hiltAndroid)
    kapt(Dep.hiltCompiler)
    implementation(Dep.hiltNavigationCompose)


    implementation(Dep.dataStore)
    implementation(Dep.protobufJavaLite)

    implementation(Dep.kotlinxDateTime)
}
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().each { task ->
            task.builtins {
                java {
                    option 'lite'
                }
            }
        }
    }
}