// Top-level build file where you can add configuration options common to all sub-projects/modules.
//room
buildscript {
    extra.apply {
        set("room_version", "2.6.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

//결제
repositories {
//    jcenter()
//    mavenCentral()
//    maven { setUrl("https://jitpack.io") }
//    maven { setUrl("https://maven.fabric.io/public") }
//    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
//    maven { setUrl("http://repository.jetbrains.com/all") }
//    gradleScriptKotlin()
}