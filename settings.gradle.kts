pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WrapperExoPlayer"
include(":app")

val mediaDir = file("media")

gradle.extra["androidxMediaSettingsDir"] = mediaDir
gradle.extra["androidxMediaModulePrefix"] = "media3"

include(
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
    ":media3lib-inspector-frame",
)

project(":media3lib-common").projectDir = File(mediaDir, "libraries/common")
project(":media3lib-exoplayer").projectDir = File(mediaDir, "libraries/exoplayer")
project(":media3lib-datasource").projectDir = File(mediaDir, "libraries/datasource")
project(":media3lib-extractor").projectDir = File(mediaDir, "libraries/extractor")
project(":media3lib-decoder").projectDir = File(mediaDir, "libraries/decoder")
project(":media3lib-container").projectDir = File(mediaDir, "libraries/container")
project(":media3lib-ui").projectDir = File(mediaDir, "libraries/ui")
project(":media3test-utils").projectDir = File(mediaDir, "libraries/test_utils")
project(":media3test-utils-robolectric").projectDir = File(mediaDir, "libraries/test_utils_robolectric")
project(":media3lib-database").projectDir = File(mediaDir, "libraries/database")
project(":media3test-data").projectDir = File(mediaDir, "libraries/test_data")
project(":media3lib-inspector").projectDir = File(mediaDir, "libraries/inspector")
project(":media3lib-effect").projectDir = File(mediaDir, "libraries/effect")
project(":media3lib-transformer").projectDir = File(mediaDir, "libraries/transformer")
project(":media3lib-muxer").projectDir = File(mediaDir, "libraries/muxer")
project(":media3lib-exoplayer-dash").projectDir = File(mediaDir, "libraries/exoplayer_dash")
project(":media3lib-inspector-frame").projectDir = File(mediaDir, "libraries/inspector_frame")