
plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "me.miksu"
version = "1.0.0"


repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.skriptlang.org/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("com.github.SkriptLang:Skript:2.12.2")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    runServer {
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to project.version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
