import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//extra["kt_ver"] = "1.6.21"
//extra["logback"] = "1.2.3"

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
}

group = "me.dun"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val logback = "1.2.9"
dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("io.netty:netty-all:4.1.86.Final")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.65")
    implementation("ch.qos.logback:logback-classic:$logback")
    implementation("ch.qos.logback:logback-core:$logback")
    implementation("ch.qos.logback:logback-access:$logback")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ComposeDeskDemo"
            packageVersion = "1.0.0"
        }
    }
}