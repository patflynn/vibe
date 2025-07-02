# CLAUDE.md - Guidelines for Agents Working in This Repository

## Build & Test Commands
- Build project: `./gradlew build`
- Run tests: `./gradlew test`
- Run single test: `./gradlew test --tests 'packageName.ClassName.methodName'`
- Install debug APK: `./gradlew installDebug`
- Android lint: `./gradlew lint`
- **Visual verification**: `./scripts/verify_ui.sh` (requires emulator running)

## Code Style Guidelines
- **Kotlin Version**: 1.9.0+, Java 17 compatibility
- **Naming**: CamelCase for classes, lowerCamelCase for variables/functions
- **Formatting**: 4-space indentation, 120 character max line length
- **Imports**: Ordered groups: Android, Kotlin, Java, other packages  
- **Error Handling**: Use try/catch for recoverable errors, with informative user messages
- **Resource Management**: Close resources in onDestroy(), use lifecycle-aware components
- **Lifecycle**: Carefully manage Activity/Fragment lifecycles, use viewBinding
- **UI Components**: Use Material Design components and ConstraintLayout
- **Architecture**: Follow single-responsibility principle with clean separation of concerns

## Workflow Guidelines
- **NEVER push directly to main** - Always create changes through a PR flow, even for small fixes
- Always sync with upstream main before starting work on a new PR (`git fetch origin main && git merge origin/main`)
- Always check PR status before pushing additional changes (`git fetch && git branch -vv`)
- Create feature branches with descriptive names for all changes (e.g., `fix/androidtest-compilation-error`)
- Submit pull requests for review before merging to main
- **Even urgent fixes must go through PR process** - respect branch protection rules
- Make sure that you keep separate issues/features in separate PRs
- Make sure to update issues with analysis, design considerations, and plan of action
- Prefer using local testing using the android SDK device manager to save time and money. Only use Firebase when you need to

## Learning & Improvement Guidelines
- **Regular reflection**: At the end of each PR, take stock of what has been learned in the session
- **Suggest new directives**: Based on mistakes, patterns, or improvements discovered, propose updates to CLAUDE.md
- **Document patterns**: When encountering repeated issues or solutions, suggest adding them to guidelines
- **Workflow improvements**: If better development practices are discovered, propose incorporating them
- **Error prevention**: After any mistake (like direct main pushes), suggest preventive measures for the future

## Visual Verification Workflow
When making UI changes, always verify visually before creating PR:

### Setup (One-time)
1. Start Android emulator: `emulator -avd Pixel_8_API_34 -no-snapshot-load`
2. Or use Android Studio Device Manager to start any API 30+ device

### Development Loop for UI Changes
1. Make UI changes to layouts, styles, or positioning code
2. Build and test: `./gradlew build && ./gradlew test`
3. **Visual verification**: `./scripts/verify_ui.sh`
4. Review screenshots in `screenshots/` folder for:
   - Settings gear visible and properly positioned
   - No overlap with status bar or system UI
   - Controls show/hide functionality works
   - Dialogs and interactions work correctly
5. If issues found, make adjustments and repeat from step 2
6. When satisfied, create PR

### Screenshots Generated
The verification script captures:
- `main_screen_TIMESTAMP.png` - Default app state
- `controls_visible_TIMESTAMP.png` - With controls shown
- `settings_dialog_TIMESTAMP.png` - Settings dialog open

### Manual Review Checklist
- Settings gear visible in top-right corner?
- Settings gear not overlapping with status bar?
- Settings gear not positioned too low?
- Controls show/hide properly when tapping screen?
- Settings dialog opens when tapping gear?
