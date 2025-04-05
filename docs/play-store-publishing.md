# Play Store Publishing Guide

This guide explains how to use the automated Play Store publishing workflow for the Vibe Android app.

## Prerequisites

Before you can use the Play Store publishing workflow, you need to ensure the following:

1. You have the necessary GitHub secrets configured:
   - `SIGNING_KEY`: Base64-encoded Android keystore file for app signing
   - `KEY_ALIAS`: The alias used in the keystore
   - `KEY_PASSWORD`: The password for the key
   - `STORE_PASSWORD`: The password for the keystore
   - `PLAY_CONFIG_JSON`: The Google Play Service Account JSON key

## Publishing Methods

There are two ways to publish a new version to the Play Store:

### Method 1: Using GitHub Releases

1. Go to the GitHub repository
2. Click on "Releases" in the sidebar
3. Click "Create a new release"
4. Fill in the tag version (e.g., `v1.2.0`)
5. Add a title and description (the description will be used as release notes)
6. Click "Publish release"

This will automatically trigger the Play Store publishing workflow and upload the app to the internal testing track.

### Method 2: Using Workflow Dispatch

For more control over the release process:

1. Go to the GitHub repository
2. Click on "Actions" in the top navigation
3. Select "Play Store Publishing" workflow
4. Click "Run workflow"
5. Fill in the required parameters:
   - **Version name**: Semantic version (e.g., `1.2.0`)
   - **Version code**: Integer that must increase with each release
   - **Track**: Which Play Store track to publish to (internal, alpha, beta, production)
   - **User fraction**: For production staged rollouts, the percentage of users (0.1 = 10%)
   - **Release notes**: Notes for this release

6. Click "Run workflow"

## Release Tracks

The workflow supports all Play Store release tracks:

- **Internal**: For initial testing within your team (default)
- **Alpha**: For early testing with trusted testers
- **Beta**: For wider testing with beta testers
- **Production**: For releasing to all users

## Staged Rollouts

For production releases, you can use staged rollouts to gradually release to users:

1. Select "production" as the track
2. Set a user fraction between 0.0 and 1.0 (0.1 = 10% of users)

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
5. For Play Store API issues, verify the service account has the correct permissions