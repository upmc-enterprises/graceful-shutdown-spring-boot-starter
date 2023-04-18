
import org.gradle.jvm.toolchain.JvmVendorSpec.ADOPTIUM
import java.util.Base64

plugins {
  id("com.diffplug.spotless") version "6.17.0"
  id("maven-publish")
  id("signing")
}

allprojects {
  repositories {
    mavenCentral()
  }
}

subprojects {
  apply {
    plugin("java")
    plugin("maven-publish")
    plugin("signing")
  }

  group = "io.github.upmc-enterprises"
  version = "2.0.0"
  extra["spring-boot.version"] = "3.0.5"

  configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
      vendor.set(ADOPTIUM)
    }
    withSourcesJar()
    withJavadocJar()
  }

  configure<SigningExtension> {
    val signingKeyProperty = findProperty("signingKey") as String?
    val signingKeyPassword = findProperty("signingPassword") as String?

    if (signingKeyProperty != null && signingKeyPassword != null) {
      val signingKey = String(Base64.getDecoder().decode(signingKeyProperty))

      val extension = project.extensions.create("SigningExtension", SigningExtension::class)
      extension.useInMemoryPgpKeys(signingKey, signingKeyPassword)
      sign(publishing.publications)
    } else {
      logger.quiet("Cannot find signingKey and/or signingPassword. Publications will not be signed.")
    }
  }

  configure<PublishingExtension> {
    repositories {
      maven {
        if (project.hasProperty("CI")) {
          val releasesRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/releases")
          val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
          url = if (project.hasProperty("release")) releasesRepoUrl else snapshotsRepoUrl

          credentials {
            username = findProperty("ossrhUsername").toString()
            password = findProperty("ossrhPassword").toString()
          }
        } else {
          url = if (project.hasProperty("release")) {
            layout.buildDirectory.dir("repos/releases")
              .get().asFile.toURI()
          } else {
            layout.buildDirectory.dir("repos/snapshots").get().asFile.toURI()
          }
        }
        logger.lifecycle("Publishing artifacts to {}", url)
      }
    }

    publications {
      create<MavenPublication>("mavenJava") {
        from(components["java"])
        versionMapping {
          usage("java-api") {
            fromResolutionOf(configurations.get("runtimeClasspath"))
          }
          usage("java-runtime") {
            fromResolutionResult()
          }
        }
        afterEvaluate {
          artifactId = "upmc-enterprises-graceful-shutdown-spring-boot-${project.base.archivesName.get()}"
          version = if (project.hasProperty("release")) version else "$version-SNAPSHOT"
        }
        pom {
          name.set("UPMC Enterprises Graceful Shutdown Spring Boot Starter")
          description.set("A Spring Boot starter that enables Spring's graceful shutdown support and supplies actuator endpoints that can be used as a preStop hook for Kubernetes.")
          url.set("https://github.com/upmc-enterprises/graceful-shutdown-spring-boot-starter")
          licenses {
            license {
              name.set("MIT License")
              url.set("http://www.opensource.org/licenses/mit-license.php")
            }
          }
          scm {
            url.set("https://github.com/upmc-enterprises/graceful-shutdown-spring-boot-starter")
          }
          developers {
            developer {
              name.set("Bill Koch")
              email.set("kochwm@upmc.edu")
              organization.set("UPMC Enterprises")
              organizationUrl.set("https://enterprises.upmc.com")
            }
          }
        }
      }
    }
  }
}

spotless {
  java {
    target("**/*.java")
    googleJavaFormat()
    toggleOffOn()
    endWithNewline()
  }
  kotlinGradle {
    ktlint("0.47.1").editorConfigOverride(mapOf("ktlint_code_style" to "official", "indent_size" to "2", "indent_style" to "space"))
    toggleOffOn()
    endWithNewline()
  }
  freshmark {
    target("**/*.md")
    indentWithSpaces(2)
    endWithNewline()
  }
}
