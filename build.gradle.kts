plugins {
    java
    application
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "fr.swansky"
version = "1.0.0"

application {
    mainClassName = "fr.swansky.radioRythm.RadioRythm"
    mainClass.set("fr.swansky.radioRythm.RadioRythm")
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://maven.zoltowski.fr/releases")
}
configurations {

}
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    api("fr.swansky:DiscordCommandIOC:1.0.8")
    api("net.dv8tion:JDA:4.3.0_346")
    api("fr.swansky:SwansIOCContainer:1.0.6")
    api("com.sedmelluq:lavaplayer:1.3.77")
    api("com.google.code.gson:gson:2.8.9")
    api("log4j:log4j:1.2.17")
    api("fr.swansky:SwansAPI:1.0.4")
}
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fr.swansky.radioRythm.RadioRythm"
    }
}






