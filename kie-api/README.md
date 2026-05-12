# KIE API

The KIE Public API module provides the core interfaces and classes for the Drools and jBPM rule engines. This API is designed to be backwards compatible between releases.

## API Compatibility Checking

This module uses [Revapi](https://revapi.org/) to ensure API compatibility between releases.

### Purpose
Revapi automatically detects breaking changes in the public API by comparing the current code against a previous release, helping maintain backwards compatibility.

### Usage

The `revapi.latestVersion` property defines which version to compare against. The current version is defined in `build-parent/pom.xml`.

```bash
# Check API compatibility (uses revapi.latestVersion from global settings)
mvn clean verify

# Or specify version explicitly (check build-parent/pom.xml for current version)
mvn clean verify -Drevapi.latestVersion=<version>

# Generate detailed HTML report
mvn clean package -Drevapi.latestVersion=<version>
# Report: target/site/revapi-report.html
```

### Handling API Changes

If Revapi detects API changes and you need to make **intentional** breaking or non-breaking changes:

1. Review the reported changes carefully
2. Update `src/build/revapi-config.json` to approve the changes
3. Add each change to the `ignore` array with:
   - Description of the change
   - Justification explaining why it's necessary

**Example:**
```json
{
  "ignores": {
    "revapi": {
      "ignore": [
        {
          "code": "java.method.addedToInterface",
          "new": "method void org.kie.api.KieSession::newMethod()",
          "justification": "kie-issues#xyz: Added new method for feature X"
        }
      ]
    }
  }
}
```

### Configuration
- **Config file**: `src/build/revapi-config.json`
- **Excludes**: Internal packages (`org.kie.internal.*`)
- **Tracks**: Deprecations and API changes

For detailed documentation, see:
- [REVAPI-USAGE.md](REVAPI-USAGE.md) - Quick start guide
- [REVAPI-CONFIGURATION.md](REVAPI-CONFIGURATION.md) - Configuration options
- [API-STABILITY-GUIDE.md](API-STABILITY-GUIDE.md) - API stability guidelines