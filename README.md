# Vibe

An ultra simple, free meditation app for Android.

## Features

- Elegant splash screen for a calming intro experience
- Simple and clean interface focused on meditation
- Meditation timer with customizable duration (5, 10, 15, 20, 25, 30, or 40 minutes)
- Meditation bell sounds and gentle vibration at the start and end of sessions
- Keeps screen active during meditation with wake lock
- Minimal permissions required (only vibration)

## App Description

A minimalist meditation timer with no ads, no tracking, and a clean design. 
Perfect for those who want a distraction-free meditation experience.

## Demo

<div align="center">
  <img src="assets/videos/app_demo.gif" width="280" alt="App Demo" />
</div>

## Download

You can download the latest APK directly from the [GitHub Releases](https://github.com/patflynn/vibe/releases) page.

Each merge to the main branch automatically creates a new release with a timestamped version and downloadable APK.

## Development Setup

### Standard Setup
1. Clone the repository
2. Open the project in Android Studio
3. Build and run on your device or emulator

### Nix Setup (Recommended for NixOS users)
This project includes a `flake.nix` for a reproducible development environment.

1. Ensure you have Nix installed with Flakes enabled.
2. Run `nix develop` or use `direnv allow`.
3. Use the `gradlew` command (provided as a shell function) which includes necessary fixes for NixOS:
   ```bash
   gradlew assembleDebug
   ```

### Google Services Configuration
The project requires a `google-services.json` file in the `app/` directory to build. 
For local development, a dummy file is provided, but for full Firebase functionality (Analytics, etc.):
1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name `dev.broken.app.vibe`.
3. Download the `google-services.json` and place it in the `app/` directory.

## Building the Project

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

## CI Status

[![Android CI](https://github.com/patflynn/vibe/actions/workflows/android-ci.yml/badge.svg)](https://github.com/patflynn/vibe/actions/workflows/android-ci.yml)

The CI pipeline:
- Builds and tests the app on every push and pull request
- Creates a downloadable APK artifact for every build
- Automatically publishes a new GitHub Release with the APK for merges to master
