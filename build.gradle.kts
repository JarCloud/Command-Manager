plugins {
  id("template.java-conventions")
  id("io.freefair.lombok") version "8.4"
}

dependencies {
  compileOnly("com.github.Minestom:Minestom:5c23713c03")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))