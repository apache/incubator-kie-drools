# drools-legacy-test-util

This module is created for a transition period when `drools-mvel` will be refactored/removed.

Most of tests in `drools-mvel` have been migrated to `test-compiler-integration` but some modules (e.g. `drools-serialization-protobuf`, `drools-persistence-jpa`) have a dependency to drools-mvel test-jar. It's not possible to have a dependency to `test-compiler-integration` because of a circular dependency. So we have moved such dependent classes and resources to a separated `drools-legacy-test-util`.

In a mid-and-long-term, we will brush up those related test cases and will likely be able to remove this `drools-legacy-test-util` module.
