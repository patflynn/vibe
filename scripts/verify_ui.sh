#!/bin/bash

# UI Verification Script for Development
# Run this script to visually verify UI changes during development
# Usage: ./scripts/verify_ui.sh

set -e

echo "🔧 Building and verifying UI changes..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Create screenshots directory if it doesn't exist
mkdir -p screenshots

echo -e "${BLUE}📱 Step 1: Building debug APK...${NC}"
./gradlew assembleDebug

echo -e "${BLUE}📱 Step 2: Checking emulator status...${NC}"

# Check if adb is available
if ! command -v adb >/dev/null 2>&1; then
    echo -e "${RED}❌ ADB not found. Please ensure Android SDK is installed and adb is in PATH${NC}"
    echo "   Add Android SDK platform-tools to PATH, e.g.:"
    echo "   export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
    exit 1
fi

# Check if emulator is running
if ! adb devices | grep -q "emulator"; then
    echo -e "${YELLOW}⚠️  No emulator running. Please start an emulator first:${NC}"
    echo "   emulator -avd Pixel_8_API_34 -no-snapshot-load"
    echo "   Or use Android Studio Device Manager"
    exit 1
fi

echo -e "${BLUE}📱 Step 3: Installing app...${NC}"
./gradlew installDebug

echo -e "${BLUE}📱 Step 4: Launching app and taking screenshots...${NC}"
# Ensure we're on home screen first
adb shell input keyevent KEYCODE_HOME
sleep 1

# Launch the app
adb shell am start -n dev.broken.app.vibe/.MainActivity
sleep 3

# Take main screen screenshot
echo "📸 Capturing main screen..."
adb shell screencap -p /sdcard/main_screen.png
adb pull /sdcard/main_screen.png screenshots/main_screen_$(date +%Y%m%d_%H%M%S).png

# Take screenshot with controls visible (tap to show if hidden)
echo "📸 Capturing with controls visible..."
adb shell input tap 500 1000  # Tap center of screen to show controls
sleep 1
adb shell screencap -p /sdcard/controls_visible.png
adb pull /sdcard/controls_visible.png screenshots/controls_visible_$(date +%Y%m%d_%H%M%S).png

# Take screenshot of settings dialog
echo "📸 Capturing settings dialog..."
# Find settings button coordinates (approximate for common screen sizes)
adb shell input tap 950 100  # Top-right area where settings button should be
sleep 2
adb shell screencap -p /sdcard/settings_dialog.png
adb pull /sdcard/settings_dialog.png screenshots/settings_dialog_$(date +%Y%m%d_%H%M%S).png

# Close dialog and app
adb shell input keyevent KEYCODE_BACK  # Close dialog
sleep 1
adb shell input keyevent KEYCODE_BACK  # Close app

echo -e "${GREEN}✅ UI verification complete!${NC}"
echo -e "${BLUE}📸 Screenshots saved to: screenshots/${NC}"
ls -la screenshots/*.png | tail -3

echo ""
echo -e "${YELLOW}🔍 Manual review checklist:${NC}"
echo "   1. Settings gear visible in top-right corner?"
echo "   2. Settings gear not overlapping with status bar?"
echo "   3. Settings gear not positioned too low?"
echo "   4. Controls show/hide properly when tapping screen?"
echo "   5. Settings dialog opens when tapping gear?"
echo ""
echo -e "${BLUE}💡 Tip: Open screenshots folder to review images${NC}"

# Optional: Open screenshots folder (works on macOS/Linux with GUI)
if command -v open >/dev/null 2>&1; then
    echo "Opening screenshots folder..."
    open screenshots/
elif command -v xdg-open >/dev/null 2>&1; then
    echo "Opening screenshots folder..."
    xdg-open screenshots/
fi