plugins {
    kotlin("jvm") version "1.3.0-rc-146"
}

allprojects {
    group ="thoughtworks.kata.gossip-bs"
    version = "1.0"
}

repositories {
    maven(url = "http://dl.bintray.com/kotlin/kotlin-eap")
    jcenter()
    mavenCentral()
}

val arrowVersion = "0.8.2"
dependencies {
    compile("io.arrow-kt:arrow-core:$arrowVersion")
    compile("io.arrow-kt:arrow-syntax:$arrowVersion")
    compile("io.arrow-kt:arrow-typeclasses:$arrowVersion")
    compile("io.arrow-kt:arrow-data:$arrowVersion")
    compile("io.arrow-kt:arrow-instances-core:$arrowVersion")
    compile("io.arrow-kt:arrow-instances-data:$arrowVersion")
    kapt("io.arrow-kt:arrow-annotations-processor:$arrowVersion")

    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.0")
}


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}


