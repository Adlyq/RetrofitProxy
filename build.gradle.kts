import java.net.URI
import java.util.*

plugins {
    kotlin("jvm") version "1.8.0"
    `java-library`
    `maven-publish`
}

group = "ml.adlyq"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    testImplementation("com.squareup.retrofit2:retrofit:2.9.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    compileOnly("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    compileOnly("com.squareup.retrofit2:retrofit:2.9.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "retrofit2-proxy"
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "github"
            url = URI("https://maven.pkg.github.com/Adlyq/RetrofitProxy")
            credentials {
                val prop = Properties().apply {
                    load(File("local.properties").inputStream())
                }
                username = prop["github.user"]?.toString()
                password = prop["github.token"]?.toString()
            }
        }
    }
}