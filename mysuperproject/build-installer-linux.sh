#!/bin/bash
echo "Building Fat JAR..."
./mvnw clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Failed to build Fat JAR."
    exit 1
fi

echo "Generating Linux DEB Installer..."
jpackage \
  --type deb \
  --name "smart-spelling-trainer" \
  --app-version "1.0.0" \
  --vendor "Nazarii Povkhanych" \
  --input target/ \
  --main-jar mysuperproject-fat.jar \
  --main-class com.mysuperproject.Launcher \
  --dest dist/ \
  --linux-shortcut

if [ $? -ne 0 ]; then
    echo "Failed to build Linux installer."
    exit 1
fi

echo "Success! Check the 'dist' folder."
