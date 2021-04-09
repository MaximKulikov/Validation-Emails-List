import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   kotlin("jvm") version "1.4.20"
   id("org.jetbrains.compose") version "0.2.0-build132"
}

group = "ru.gavarent"
version = "1.3"

repositories {
   jcenter()
   mavenCentral()
   maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
   implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
   kotlinOptions.jvmTarget = "11"
}

compose.desktop {
   application {
      mainClass = "MainKt"
      nativeDistributions {
         version = "1.3"
         targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
         packageName = "email-validation"
         this.windows {
            dirChooser = true
            shortcut = true
            menuGroup = "Email"
            menu = true
         }
      }
   }
}