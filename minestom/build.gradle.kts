plugins {
  id("template.java-conventions")
  id("io.freefair.lombok") version "8.4"
}

dependencies {
  compileOnly("net.minestom:minestom-snapshots:2be6f9c507")
  implementation(project(":server:command:core"))
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))