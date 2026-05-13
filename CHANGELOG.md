# Changelog

All notable changes to the Apache KIE Drools project will be documented in this file.

## [Unreleased]

### Added
- YAML property retrieval support (issue #2163, PR #6662)

### Changed
- Upgraded Quarkus to 3.27.3 (PR #6673)
- Upgraded kafka-clients to 4.1.2 (PR #6671)

### Removed
- YaRD and Serverless Workflow support (issue #2285, PR #6663)

### Security
- Fixed CVE-2026-33870, CVE-2026-33871: Upgraded netty-codec-http to 4.1.131.Final (PR #6673)
- Fixed CVE-2025-67030: Upgraded plexus-utils to 3.6.1 (PR #6670)
- Fixed CVE-2025-67721: Upgraded Jackson core to 2.21.1 (PR #6672)
- Fixed CVE-2026-1002: Upgraded vertx-core to 4.5.24 (PR #6636)
- Fixed CVE-2022-41853: Upgraded HSQLDB to 2.7.1 (PR #6563)

### Fixed
- PseudoClockScheduler memory leak: Now retains max 1000 canceled jobs (issue #6659, PR #6660)
- Flaky PersistenceTest by disabling parallel execution (PR #6675)

## [10.2.0] - 2026-03-28

### Added
- Question mark operator support in decision table unary test expressions (PR #6577)
- Configurable compile-time warning for self-referencing constraints (PR #6618)
- Validation for fact types used in rules (PR #6524)
- Test Scenario Runner adapted to JUnit 5 Test Engine (issue #1678, PR #6338)
- DMN error handling modes (issue #2015, PR #6387)
- Support for re-scheduling Process Instances and Node Instances SLA timers (issue #1965, PR #6358)
- PathUtils implementation (issue #1886, PR #6355)
- New date and time conversion functions (issue #1906, PR #6303)

### Changed
- **kie-api**: API changes - see [kie-api/CHANGELOG.md](kie-api/CHANGELOG.md#1020---2026-03-28)
- Upgraded Quarkus to 3.27.2 (issue #2204, PR #6575)
- Upgraded Maven to 3.9.11 (PR #6409)
- Upgraded Spring Boot to 3.4.11 (PR #6502)
- Upgraded testcontainers to 2.0.3 (issue #2187, PR #6552)
- Migrated Apache licenses check to apache-rat 0.17.x (issue #1670, PR #6550)

### Security
- Fixed path traversal vulnerability (issue #2265, PR #6615)
- Fixed multiple CVEs via dependency upgrades (PR #6544)
- Upgraded tomcat-dbcp to 10.1.48 (PR #6505)
- Upgraded vertx-web to 4.5.22 (PR #6500)
- Upgraded logback to 1.5.19 (PR #6485)

### Fixed
- DMN TCK unary test 005 returning null (issue #2210, PR #6556)
- DMN missing evaluationHitIds in decision tables (issue #2190, PR #6553)
- DMN missing evaluationHitIds in conditional expressions (issue #2226, PR #6568)
- Null pointer with exists() clauses (issue #2235, PR #6585)
- Activation-group not applied after rule fires (issue #6509, PR #6517)
- Rules in agenda group with auto-focus issues (issue #6421, PR #6449, #6447)
- Multiple rules in activation group firing (issue #6410, PR #6419)
- Conditional named consequence with break (issue #2105, PR #6461)
- Parser exception with leading zeros in DRL10 (issue #6539, PR #6541)
- Executable-model missing passive flag (issue #6529, PR #6530)
- kJar code generation not failing on unknown field in OOPath (issue #6514, PR #6518)

## [10.1.0] - 2025-04-30

### Added
- DMN 1.6 support
- New Antlr4 DRL parser (disabled by default) (issue #5988, PR #6225)
- DMN B-FEEL implementation (issue #1742, PR #6213)
- Support for generic range types in DMN engine (PR #6123)
- DMNEvent reporting in DMNConditionalEvaluator (issue #1528, PR #6124)
- Rule IDs in AfterEvaluateDecisionTableEvent (issue #1543, PR #6127)
- Multi-module and indexed dependencies support (issue #6274, PR #6278)
- Support for BigInteger arithmetic operations with executable-model (issue #6253, PR #6276)
- Support for traits to inherit multiple traits (PR #5824)
- Support for trait type declaration (PR #5820)

### Changed
- **kie-api**: API changes - see [kie-api/CHANGELOG.md](kie-api/CHANGELOG.md#1010---2025-04-30)
- Upgraded Quarkus to 3.15 (issue #1575, PR #6131)
- Upgraded MVEL to 2.5.2.Final (PR #6226)
- Upgraded logback to 1.5.16 (issue #1787, PR #6236)
- Upgraded XStream to 1.4.21 (PR #6148)
- Upgraded protobuf-java (PR #6094)
- Removed drools-docs from repository (Apache policy compliance, PR #6287)
- Cleaned up legacy change-set support (issue #6163, PR #6188)
- Cleaned up legacy .rf format support (issue #6160, PR #6189)

### Security
- Fixed CVE-2023-0833: Upgraded okhttp to address vulnerability (issue #1737, PR #6230)
- Fixed netty-related CVE (PR #6263)
- Upgraded logback-core from 1.4.14 to 1.5.13 (PR #6208)

### Fixed
- DMN TCK range equality test failures for unary test ranges (issue #1546, PR #6134)
- DMN type check for decision service input parameters (issue #1410, PR #6133)
- DMN namespace alignment verification (issue #1834, PR #6259)
- DMN zero-seconds truncation on date time (issue #1829, PR #6255)
- DMN evaluation hit IDs mapping in nested conditional/decision table elements (issue #1853, PR #6271)
- DMN evaluation hit ID mapping to belonging decision (issue #1791, PR #6242)
- DMN range comparison failures in FEEL with unary test ranges (issue #1744, PR #6231)
- DMN range-related TCK failures (issue #1794, PR #6244)
- DMN range starting with null not permitted (issue #1811, PR #6266)
- DMN missing conditional element runtime exception (issue #1820, PR #6257)
- DMN lambda function returning null TCK failure (issue #1752, PR #6261)
- DMN return list of dates instead of range (issue #1743, PR #6223)
- Test Scenario version attribute not correctly retrieved (issue #1807, PR #6254)
- BooleanEvalHelper class cast exceptions (issue #1771, PR #6229)
- Group node validation failure (issue #1832, PR #6262)
- Property reactivity not reacting to super class properties overridden by sub class getter (issue #6243, PR #6251)
- Removal of detached tuples during incremental compilation (issue #6190, PR #6192)
- ForkJoinPool context ClassLoader inheritance (issue #1738, PR #6211)
- SessionConfiguration loading at startup in project templates (issue #1753, PR #6218)
- DroolsAssetsProcessor exception when no DRL resources present (issue #6273, PR #6277)
- Accumulate min evaluation (issue #6180, PR #6186)
- KieServiceLoader thread-safety (issue #1662, PR #6177)

## [10.0.0] - 2024-12-10

### Added
- Initial release
- **kie-api**: Initial API - see [kie-api/CHANGELOG.md](kie-api/CHANGELOG.md#1000---2024-12-10)

[Unreleased]: https://github.com/apache/incubator-kie-drools/compare/10.2.0...HEAD
[10.2.0]: https://github.com/apache/incubator-kie-drools/compare/10.1.0...10.2.0
[10.1.0]: https://github.com/apache/incubator-kie-drools/compare/10.0.0...10.1.0
[10.0.0]: https://github.com/apache/incubator-kie-drools/releases/tag/10.0.0