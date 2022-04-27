plugins {
    id("java")
}

group = "org.virep"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.11")
    implementation("io.github.cdimascio:java-dotenv:5.2.2")
    implementation("com.github.Xirado:Lavalink-Client:master-SNAPSHOT") {
        exclude (group = "com.sedmelluq", module = "lavaplayer")
    }
    implementation("com.github.walkyst:lavaplayer-fork:1.3.97")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    implementation("org.reflections:reflections:0.10.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}