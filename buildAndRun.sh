#!/bin/bash

echo "Building"
javac -classpath .:classes:lib/* LedControl.java
echo "Running"
java -Dpi4j.linking=dynamic -classpath .:classes:lib/* LedControl
echo "Done"
