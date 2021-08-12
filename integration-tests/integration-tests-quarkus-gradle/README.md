Quarkus Gradle Integration Test
===============================

This project will run `./gradlew --no-daemon quarkusBuild` in a specific subdirectory,
configured in the Maven property `${gradle.project.dir}` using the Maven exec plugin.

You may duplicate this project to add more test cases.
