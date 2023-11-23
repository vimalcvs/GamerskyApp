plugins {
    id("com.android.application")
}

android {
    namespace = "com.news.gamersky"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.news.gamersky"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")



    implementation ("androidx.room:room-runtime:2.6.0")
    annotationProcessor ("androidx.room:room-compiler:2.6.0")


    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")

    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.navigation:navigation-fragment:2.7.5")
    implementation ("androidx.navigation:navigation-ui:2.7.5")
    implementation ("androidx.preference:preference:1.2.1")


    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.billy.android:smart-swipe:1.1.2")

    implementation ("com.github.piasy:BigImageViewer:1.8.1")
    implementation ("com.github.piasy:GlideImageLoader:1.8.1")
    implementation ("com.github.piasy:GlideImageViewFactory:1.8.1")

    implementation ("org.jsoup:jsoup:1.13.1")

}