plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.meooo27.wrapperexoplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.meooo27.wrapperexoplayer"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":media3lib-exoplayer"))
    implementation(project(":media3lib-common"))
    implementation(project(":media3lib-datasource"))
}

tasks.register<Jar>("fatJar") {

    archiveBaseName.set("simpleplayer-fat")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val modules = listOf(
        ":media3lib-common",
        ":media3lib-exoplayer",
        ":media3lib-datasource",
        ":media3lib-extractor",
        ":media3lib-decoder",
        ":media3lib-container",
        ":media3lib-ui",
        ":media3test-utils",
        ":media3test-utils-robolectric",
        ":media3lib-database",
        ":media3test-data",
        ":media3lib-inspector",
        ":media3lib-effect",
        ":media3lib-transformer",
        ":media3lib-muxer",
        ":media3lib-exoplayer-dash",
    )

    // 🔥 đảm bảo build trước
    dependsOn(modules.map { "$it:assembleRelease" })
    dependsOn("assembleRelease")

    doFirst {

        val buildDir = layout.buildDirectory.get().asFile

        // class app
        from("${buildDir}/intermediates/javac/release/classes")
        from("${buildDir}/tmp/kotlin-classes/release")

        modules.forEach { path ->

            val aarFile = project(path)
                .layout.buildDirectory
                .file("outputs/aar/${project(path).name}-release.aar")
                .get()
                .asFile

            if (!aarFile.exists()) {
                throw GradleException("AAR not found: $aarFile")
            }

            // extract classes.jar trong AAR
            from(zipTree(aarFile).matching {
                include("classes.jar")
            }.map { zipTree(it) })
        }
    }

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
}