plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.1"
    id("io.micronaut.aot") version "4.2.1"
    id("org.jooq.jooq-codegen-gradle") version "3.19.0"
}

version = "0.1"
group = "com.example"

repositories {
    mavenCentral()
}

// https://www.jooq.org/doc/3.19/manual/code-generation/codegen-gradle/
// https://www.jooq.org/doc/3.19/manual/code-generation/codegen-configuration/
// https://www.jooq.org/doc/3.19/manual/getting-started/tutorials/jooq-with-flyway/
jooq {

    configuration {

        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:5432/postgres"
            user = "postgres"
            password = ""

            // You can also pass user/password and other JDBC properties in the optional properties tag:
//            properties {
//                property {
//                    key = "user"
//                    value = "[db-user]"
//                }
//                property {
//                    key = "password"
//                    value = "[db-password]"
//                }
//            }
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                includes = ".*"
                excludes = "flyway_schema_history"
                inputSchema = "public"
            }

            target {
                packageName = "example.micronaut.jooq"
                directory = "build/generated-sources/jooq"
            }
        }
    }
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.sql:micronaut-jooq")

    jooqCodegen("org.testcontainers:postgresql:1.18.3")
    jooqCodegen("org.postgresql:postgresql:42.6.0")

    implementation("org.jooq:jooq:3.19.0")

    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
}


application {
    mainClass.set("com.example.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}


graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}


