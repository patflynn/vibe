# Vibe App Automated Testing

This directory contains automated UI tests for the Vibe meditation app using Espresso and Firebase Test Lab integration.

## Test Structure

- `MainActivityTest.kt`: Basic UI tests for the main meditation screen

## Running Tests Locally

```bash
# Run all tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=dev.broken.app.vibe.MainActivityTest
```

## Firebase Test Lab Integration

Tests are automatically run on Firebase Test Lab for multiple device configurations through GitHub Actions workflow.

### Device Configurations

Tests run on the following devices:
- Pixel 2 (Android 11)
- Nexus 6P (Android 10)
- Pixel 4 (Android 12)

## Adding New Tests

When adding new tests:

1. Create a new test class or add methods to existing classes
2. Follow the Espresso testing pattern (find view → perform action → check result)
3. Keep tests independent and focused on a single feature
4. Avoid external dependencies in tests when possible

## Test Reports

Test reports from Firebase Test Lab runs are automatically collected and available as artifacts in GitHub Actions.