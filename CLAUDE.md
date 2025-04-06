# CLAUDE.md - Guidelines for Agents Working in This Repository

## Build & Test Commands
- Build project: `./gradlew build`
- Run tests: `./gradlew test`
- Run single test: `./gradlew test --tests 'packageName.ClassName.methodName'`
- Install debug APK: `./gradlew installDebug`
- Android lint: `./gradlew lint`

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
- Always create changes through a PR flow, even without being explicitly asked
- Always sync with upstream main before starting work on a new PR (`git fetch origin main && git merge origin/main`)
- Always check PR status before pushing additional changes (`git fetch && git branch -vv`)
- Create feature branches with descriptive names for all changes
- Submit pull requests for review before merging to main
- Make sure that you keep separate issues/features in separate PRs
- Make sure to update issues with analysis, design considerations, and plan of action
- Prefer using local testing using the android SDK device manager to save time and money. Only use Firebase when you need to
