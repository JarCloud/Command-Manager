plugins {
  id("java-library")
  id("io.freefair.lombok") version "8.4"
  id("com.vanniktech.maven.publish") version "0.28.0"
}

version = findProperty("tag") ?: "0.0.1-SNAPSHOT"

dependencies {
  compileOnly("org.jetbrains:annotations:24.1.0")
  compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

  implementation("me.lucko:commodore:2.2")
  implementation("io.github.mr-empee.command-forge:core:0.0.1")

  api("io.github.mr-empee.command-forge:core:0.0.1")
}

mavenPublishing {
  coordinates("io.github.mr-empee.command-forge", "bukkit", version.toString())

  pom {
    name.set("Command Forge")
    description.set("A programmatic multi-platform command framework")
    inceptionYear.set("2024")
    url.set("https://github.com/Mr-EmPee/Command-Forge")
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }

    developers {
      developer {
        id.set("mr-empee")
        name.set("Mr. EmPee")
        url.set("https://github.com/mr-empee/")
      }
    }

    scm {
      url.set("https://github.com/Mr-EmPee/Command-Forge")
      connection.set("scm:git:git://github.com/Mr-EmPee/Command-Forge.git")
      developerConnection.set("scm:git:ssh://git@github.com:Mr-EmPee/Command-Forge.git")
    }
  }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))