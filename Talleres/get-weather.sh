#!/bin/bash

cd "$(dirname "$0")"

javac -cp .:json-20231013.jar RegistroClimaticoQuito.java
java -cp .:json-20231013.jar RegistroClimaticoQuito
