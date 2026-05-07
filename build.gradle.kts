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

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val jacksonVersion: String by project
val jettyVersion: String by project
val logbackVersion: String by project
val lombokVersion: String by project
val prometheusVersion: String by project

val junitVersion: String by project
val wiremockVersion: String by project

dependencies {
    // jackson
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // jetty
    implementation("org.eclipse.jetty:jetty-client:$jettyVersion")
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")

    // logback
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // prometheus
    implementation("io.prometheus:simpleclient:${prometheusVersion}")
    implementation("io.prometheus:simpleclient_common:${prometheusVersion}")

    // lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:${junitVersion}")
    testImplementation("org.wiremock:wiremock-standalone:${wiremockVersion}")

    // lombok
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
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

tasks.jar {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    entryCompression = ZipEntryCompression.DEFLATED

    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/*-LICENSE")
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/NOTICE")
        exclude("META-INF/maven/")
        exclude("META-INF/native-image/")
        exclude("META-INF/versions/")
    }

    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Main-Class" to "ee.moo.miner.exporter.Application",
        )
    }
}
