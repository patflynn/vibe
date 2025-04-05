# Play Store Publishing Guide

This guide explains how to use the automated Play Store publishing workflows for the Vibe Android app.

## Prerequisites

Before you can use the Play Store publishing workflows, you need to ensure the following:

1. You have the necessary GitHub secrets configured:
   - `SIGNING_KEY`: Base64-encoded Android keystore file for app signing
   - `KEY_ALIAS`: The alias used in the keystore
   - `KEY_PASSWORD`: The password for the key
   - `STORE_PASSWORD`: The password for the keystore
   - `GOOGLE_SERVICES_JSON_BASE64`: Base64-encoded google-services.json file

## Publishing Workflows

There are two separate workflows for different publishing scenarios:

### 1. Testing Tracks Workflow

Use this workflow for internal, alpha, and beta releases:

- **Workflow Name**: "Play Store Testing Tracks"
- **Purpose**: Deploy to non-production tracks for testing
- **Tracks Available**: internal, alpha, beta

### 2. Production Workflow

Use this workflow for production releases:

- **Workflow Name**: "Play Store Production Release"
- **Purpose**: Deploy to the production track, with optional staged rollout
- **Special Features**: Supports staged rollouts with user fraction control

## Publishing Methods

### Method 1: Using GitHub Releases (Testing Only)

For quick testing releases:

1. Go to the GitHub repository
2. Click on "Releases" in the sidebar
3. Click "Create a new release"
4. Fill in the tag version (e.g., `v1.2.0`)
5. Add a title and description (the description will be used as release notes)
6. Click "Publish release"

This will automatically trigger the Play Store Testing Tracks workflow and upload the app to the internal testing track.

### Method 2: Using Workflow Dispatch

For more control over the release process:

#### For Testing Releases:

1. Go to the GitHub repository
2. Click on "Actions" in the top navigation
3. Select "Play Store Testing Tracks" workflow
4. Click "Run workflow"
5. Fill in the required parameters:
   - **Version name**: Semantic version (e.g., `1.2.0`)
   - **Version code**: Integer that must increase with each release
   - **Track**: Which testing track to publish to (internal, alpha, beta)
   - **Release notes**: Notes for this release (optional)

6. Click "Run workflow"

#### For Production Releases:

1. Go to the GitHub repository
2. Click on "Actions" in the top navigation
3. Select "Play Store Production Release" workflow
4. Click "Run workflow"
5. Fill in the required parameters:
   - **Version name**: Semantic version (e.g., `1.2.0`)
   - **Version code**: Integer that must increase with each release
   - **User fraction**: For staged rollouts, the percentage of users (e.g., 0.1 for 10%)
   - **Release notes**: Notes for this release (required)

6. Click "Run workflow"

## Testing Tracks

The testing workflow supports the following Play Store tracks:

- **Internal**: For initial testing within your team (default)
- **Alpha**: For early testing with trusted testers
- **Beta**: For wider testing with beta testers

## Staged Rollouts

For production releases, you can use staged rollouts to gradually release to users:

1. Launch the "Play Store Production Release" workflow
2. Set a user fraction between 0.01 and 0.99 (e.g., 0.1 for 10% of users)
3. For a full release to all users, set the user fraction to 1.0

The workflow automatically sets the appropriate status based on whether it's a staged rollout or full release.

## Monitoring Releases

After triggering a release:

1. The workflow will run and publish the app to Google Play
2. Check the workflow execution for any errors or issues
3. Go to the Google Play Console to monitor the release status
4. Wait for Google Play to process and publish the app (can take several hours)

## Troubleshooting

If you encounter issues with the publishing process:

1. Check the workflow run logs for specific error messages
2. Verify that all required secrets are correctly configured
3. Ensure your app meets Google Play requirements
4. For signing issues, make sure the keystore credentials are correct
5. For Google Cloud authentication issues, verify the Workload Identity Federation is properly configured