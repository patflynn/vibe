# CI Security Controls

This repository implements security controls for Continuous Integration (CI) workflows to prevent abuse and protect sensitive credentials.

## Why These Controls Exist

CI workflows in public repositories can be vulnerable to several security risks:

1. **Credential Exposure**: CI workflows often have access to sensitive credentials and secrets
2. **Resource Abuse**: Unauthorized use of CI resources can lead to excess costs
3. **Supply Chain Attacks**: Malicious PRs could attempt to steal secrets or manipulate builds

## How CI Security Works

### For Repository Maintainers and Collaborators

If you are a maintainer or collaborator (with repository write access):

- Your CI workflows will run automatically
- You don't need to take any special actions

### For External Contributors

If you are an external contributor:

1. When you submit a PR, CI workflows will not run automatically
2. A repository maintainer must review your PR and add the `run-ci` label
3. Once labeled, CI workflows will run on your PR

## Adding the `run-ci` Label

Repository maintainers can add the `run-ci` label to a PR via:

1. The GitHub UI: View the PR → Labels section → Select "run-ci"
2. GitHub CLI: `gh pr edit <PR-NUMBER> --add-label "run-ci"`

## Conditional CI Execution

Our workflows use various checks to determine whether CI should run:

1. **Trusted Sources**: Pushes to the main branch always run CI
2. **PR Author**: Collaborators' PRs automatically run CI
3. **PR Label**: External PRs run CI only with the `run-ci` label
4. **File Changes**: Some workflows only run for relevant code changes

## For Maintainers

When reviewing external PRs:

1. **Review code first** before adding the `run-ci` label
2. Only add the label after you're confident the PR isn't malicious
3. Be aware that adding the label grants access to CI resources and potentially sensitive build contexts

## Manual Workflow Triggers

Workflows can also be triggered manually by maintainers using GitHub's workflow_dispatch feature.