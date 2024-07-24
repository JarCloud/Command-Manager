plugins {
  id("template.java-conventions")
  id("io.freefair.lombok") version "8.4"
}

dependencies {
  compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

  implementation("me.lucko:commodore:2.2")
  implementation(project(":server:command:core"))
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))