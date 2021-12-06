# My Friends

Example LWW Graph implementation 

----

### Documentation
Documentation will be available on http://127.0.0.1:8080/q/swagger-ui/

### Try it out
```
docker-compose up
```
There will be 3 instances deployed - ports 8080, 8081, 8082

### Env Variables:
```
APP_PORT - server http port, default 8080
SYNC_INSTANCES - list of replicas to sync, default empty
SYNC_FREQUENCY - sync frequency in seconds, defualt 10
```

### Requirements

jdk11 - for simple build

jdk11 + GraalVM - for native image (tested on build 11.0.13+7-jvmci-21.3-b05), can be obtained on https://www.graalvm.org/downloads/

### Build and run
```
./gradlew clean build
java -jar build/quarkus-app/quarkus-run.jar
```

### Build native image and run
```
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true && \
./build/myfriends-1.0-runner
```

### Build native image docker
```
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true && \
docker build -f src/main/docker/Dockerfile.native-distroless . -t shaad1337/myfriends:latest
docker run --rm -p 8080:8080 -e API_PORT=8080 shaad1337/myfriends:latest
```
