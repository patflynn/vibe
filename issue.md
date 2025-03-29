## Issue

The Firebase Test Lab instrumentation tests are failing on API 30 devices (Pixel 5) with 1 test case failing and 3 passing.

## Root Cause

After investigating the code, I found that `MainActivity.kt` uses `VibratorManager` which is only available in API level 31+ (Android 12). However, our tests are running on devices starting from API level 30 (Android 11).

The specific issue is in the `playBellSound()` method where vibration is implemented using the `VibratorManager` class which was introduced in Android 12.

## Proposed Solution

We need to implement a backward-compatible solution for vibration that works on both Android 11 (API 30) and newer versions. This can be done using the compatibility APIs or creating conditional code paths based on the API level.

## Test Plan

1. Modify the vibration code to handle both API level 30 and newer versions
2. Run tests locally to verify the fix
3. Verify the Firebase Test Lab tests pass on all devices including API level 30
