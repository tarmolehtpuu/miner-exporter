import java.util.jar.Manifest

plugins {
    java
    jacoco
}

group = "ee.moo"
version = rootProject.file("VERSION").readText().trim()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/tarmolehtpuu/tiny-json")
        credentials {
            username = project.findProperty("github.user") as String? ?: System.getenv("GITHUB_USER")
            password = project.findProperty("github.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // ******************* JAVA ******************* //

    // moo
    implementation("ee.moo:tiny-json:0.0.5")
    implementation("ee.moo:tiny-prometheus:0.0.4")

    // jetty
    implementation("org.eclipse.jetty:jetty-client:12.1.9")

    // ******************* TEST ******************* //

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.3")
    testImplementation("org.junit.platform:junit-platform-launcher:6.0.3")

    // wiremock
    testImplementation("org.wiremock:wiremock-standalone:3.13.2")
}

sourceSets {
    main {
        java {
            srcDir("src/java")
        }
        resources {
            srcDir("src/resources")
        }

    }
    test {
        java {
            srcDir("test/java")
        }
        resources {
            srcDir("test/resources")
        }
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.withType<Test>())
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register("copyright") {
    description = "Generates LICENSE and NOTICE files for 3rd party dependencies"

    val cp = configurations.runtimeClasspath.get()
    val out = layout.buildDirectory.dir("generated")

    inputs.files(cp)
    outputs.dir(out)

    doLast {
        val modules = cp
            .resolvedConfiguration
            .resolvedArtifacts
            .associate { it.file.absolutePath to it.name }

        cp.files.forEach { jar ->
            val module = modules[jar.absolutePath] ?: jar.nameWithoutExtension

            zipTree(jar).matching {
                include("META-INF/LICENSE")
                include("META-INF/LICENSE.txt")
            }.forEach { file ->
                file.copyTo(out.get().file("licenses/LICENSE.$module").asFile, overwrite = true)
            }

            zipTree(jar).matching {
                include("META-INF/NOTICE")
                include("META-INF/NOTICE.txt")
            }.forEach { file ->
                file.copyTo(out.get().file("notices/NOTICE.$module").asFile, overwrite = true)
            }

            zipTree(jar).filter { it.name == "MANIFEST.MF" }.forEach { m ->
                val manifest = Manifest(m.inputStream())

                val file1 = out.get().file("licenses/LICENSE.$module").asFile
                val file2 = out.get().file("notices/NOTICE.$module").asFile

                if (!file1.exists()) {
                    val text = manifest.mainAttributes.getValue("Bundle-License")
                    if (text != null) {
                        file1.createNewFile()
                        file1.writeText("License URL: $text\n")
                    }
                }

                if (!file2.exists()) {
                    val text = manifest.mainAttributes.getValue("Bundle-Copyright")
                    if (text != null) {
                        file2.createNewFile()
                        file2.writeText("$text\n")
                    }
                }
            }
        }
    }
}


tasks.jar {
    dependsOn("copyright")

    archiveBaseName.set("app")
    archiveVersion.set("")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    entryCompression = ZipEntryCompression.DEFLATED

    // project LICENSE and NOTICE
    from(project.rootDir) {
        include("LICENSE")
        include("NOTICE")
        into("META-INF")
    }

    // 3rd party LICENSE files
    from("build/generated/licenses") {
        into("META-INF/licenses")
    }

    // 3rd party NOTICE files
    from("build/generated/notices") {
        into("META-INF/notices")
    }

    // 3rd party jars (fat JAR)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/**")
        exclude("module-info.class")
    }

    // project src
    from(sourceSets.main.get().output)

    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Main-Class" to "ee.moo.miner.exporter.Application",
            "Bundle-Copyright" to "Copyright 2026 Tarmo Lehtpuu",
            "Bundle-License" to "http://www.apache.org/licenses/LICENSE-2.0"
        )
    }
}

