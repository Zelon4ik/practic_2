#!/bin/bash
echo "Building Fat JAR..."
./mvnw clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Failed to build Fat JAR."
    exit 1
fi

echo "Generating macOS DMG Installer..."
jpackage \
  --type dmg \
  --name "StudentProgressTracker" \
  --app-version "1.0.0" \
  --vendor "Student" \
  --input target/ \
  --main-jar mysuperproject-fat.jar \
  --main-class com.mysuperproject.Launcher \
  --dest dist/ \
  --mac-package-name "student-progress-tracker"

if [ $? -ne 0 ]; then
    echo "Failed to build macOS installer."
    exit 1
fi

echo "Success! Check the 'dist' folder."
