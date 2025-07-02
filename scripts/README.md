# Development Scripts

This directory contains scripts to assist with development workflows.

## verify_ui.sh

Visual verification script for UI changes during development.

**Usage:**
```bash
./scripts/verify_ui.sh
```

**Prerequisites:**
- Android emulator running (API 30+)
- Debug APK buildable

**What it does:**
1. Builds debug APK
2. Installs app on emulator
3. Launches app and captures screenshots of key UI states
4. Saves timestamped screenshots to `screenshots/` directory

**Screenshots captured:**
- Main screen (default state)
- Controls visible state
- Settings dialog open

**Use this script whenever making UI changes to verify:**
- Settings gear positioning
- Status bar overlap issues
- Control visibility/hiding
- Dialog functionality

The script is integrated into the development workflow documented in `CLAUDE.md`.