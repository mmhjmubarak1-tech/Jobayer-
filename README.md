# Remote Camera Control App

## Overview
This app allows you to control one Android phone from another without requiring an APK on the second phone.

**Phone A (This Device):** Runs the APK - Has full camera access and streams to Phone B
**Phone B (Other Device):** Opens a web browser link - Sends control commands to Phone A

## Features
- ✅ Full camera access on Phone A
- ✅ Web-based control panel for Phone B (no APK needed)
- ✅ Works over WiFi or any network
- ✅ Works offline (local network)
- ✅ Simple button controls
- ✅ Real-time status updates

## How to Build

### Prerequisites
- Android Studio installed
- Gradle configured
- Android SDK 24+

### Steps
1. Clone the repo:
   ```bash
   git clone https://github.com/mmhjmubarak1-tech/Jobayer-.git
   cd Jobayer-
   git checkout android-app-setup
   ```

2. Open in Android Studio:
   - File → Open → Select the project folder

3. Build the APK:
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Find APK in: `app/build/outputs/apk/debug/app-debug.apk`

4. Or build from command line:
   ```bash
   ./gradlew assembleDebug
   ```

## How to Use

### On Phone A (With APK):
1. Install and open the app
2. Grant camera permissions
3. Tap "Start Camera"
4. Copy the link that appears (e.g., `http://192.168.1.100:8888`)

### On Phone B (Without APK):
1. Open web browser
2. Paste the link from Phone A
3. Use the buttons to control Phone A's camera:
   - 📷 Take Photo
   - ⚡ Toggle Flash
   - 🔍 Zoom In/Out

## Permissions Required
- Camera (full access)
- Internet
- Network access (local + internet)
- External storage (for saving photos)

## Troubleshooting

**Can't see the web interface?**
- Make sure both phones are on same WiFi network
- Check firewall settings
- Try entering the exact IP address shown in Phone A

**Camera not working?**
- Grant camera permissions
- Close other camera apps
- Restart the app

**Connection issues?**
- Check both phones have internet/network access
- Ensure firewall allows port 8888
- Try local WiFi instead of mobile data

## Next Steps
The app framework is ready. You can add:
- Video streaming (MJPEG format)
- Multi-touch controls
- Recording capabilities
- WiFi Direct support
- Cloud backup
