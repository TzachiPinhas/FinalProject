import com.android.tools.r8.internal.im

plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.finalproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation("com.google.firebase:firebase-database")
    implementation(libs.firebase.authentication.ui)
    implementation(libs.gms.play.services.auth)
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")


}