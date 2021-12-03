FROM alpine:3.14

# Install dependencies
RUN apk add openjdk11
RUN apk add maven

# Copying sources and POM file
COPY src/ /root/app/src
COPY pom.xml /root/app

# Build project
RUN mvn -f /root/app/pom.xml clean package

# Run project
ENTRYPOINT ["java","-jar","/root/app/target/wetalk-api-1.0.jar"]
