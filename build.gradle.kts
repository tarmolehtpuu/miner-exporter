plugins {
    java
}

group = "ee.moo"
version = "0.0.1"

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

    // lombok
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // prometheus
    implementation("io.prometheus:simpleclient:$prometheusVersion")
    implementation("io.prometheus:simpleclient_common:$prometheusVersion")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:${junitVersion}")
    testImplementation("org.wiremock:wiremock-standalone:${wiremockVersion}")
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

tasks.withType<Test> {
    useJUnitPlatform()
}

