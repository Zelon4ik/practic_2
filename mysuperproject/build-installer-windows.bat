@echo off
echo Building Fat JAR...
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
   echo Failed to build Fat JAR.
   pause
   exit /b %errorlevel%
)

echo Generating Windows MSI Installer...
jpackage --type msi --name "SmartSpellingTrainer" --app-version "1.0.0" --vendor "Nazarii Povkhanych" --description "Розумний тренажер правопису з моніторингом прогресу" --input target/ --main-jar mysuperproject-fat.jar --main-class com.mysuperproject.Launcher --dest dist/ --win-dir-chooser --win-menu --win-shortcut
if errorlevel 1 (
   echo Failed to build installer. Please ensure WiX Toolset v3 is installed.
   echo Download WiX v3 from: https://wixtoolset.org/docs/wix3/
   pause
   exit /b %errorlevel%
)

echo Success! Check the 'dist' folder.
pause
