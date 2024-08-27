plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.asapelectrocountingapplicationproj"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.asapelectrocountingapplicationproj"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.yukuku:ambilwarna:2.0.1")
    implementation ("com.itextpdf:itextg:5.5.10")
    implementation ("org.jsoup:jsoup:1.14.3")

}