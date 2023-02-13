import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "1.8.0"
    id("idea")
    id("com.google.protobuf") version "0.9.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.12"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.52.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

sourceSets {
    main {
        kotlin {
            srcDirs.add(File(buildDir,"/generated/source"))
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-protobuf:1.52.1")
    runtimeOnly("io.grpc:grpc-netty-shaded:1.52.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.21.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}