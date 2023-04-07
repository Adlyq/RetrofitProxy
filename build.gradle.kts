plugins {
    kotlin("jvm") version "1.8.0"
    `java-library`
    `maven-publish`
}

group = "ml.adlyq"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    compileOnly("com.squareup.retrofit2:retrofit:2.9.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
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
}