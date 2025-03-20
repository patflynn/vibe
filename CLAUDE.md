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
- Create feature branches with descriptive names for all changes
- Submit pull requests for review before merging to main