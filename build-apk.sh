#!/bin/bash

echo "Building APK..."
./gradlew clean assembleDebug

echo "APK built successfully!"
echo "Location: app/build/outputs/apk/debug/app-debug.apk"
