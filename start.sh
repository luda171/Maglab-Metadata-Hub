#!/bin/bash
export JAVA_OPTS='-Djdk.http.auth.tunneling.disabledSchemes=""'
jar -xf ./target/maglabproject-1.0-SNAPSHOT.jar
java org.springframework.boot.loader.JarLauncher