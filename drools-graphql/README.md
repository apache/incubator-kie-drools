# Drools GraphQL API for Rule Metadata

A GraphQL API for querying Drools rule definitions, execution statistics, and impact analysis results. Built on MicroProfile GraphQL annotations, compatible with SmallRye GraphQL (Quarkus) and other MicroProfile implementations.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-graphql</artifactId>
    <version>${drools.version}</version>
</dependency>

<!-- For Quarkus applications, also add the SmallRye GraphQL runtime -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-graphql</artifactId>
</dependency>
```

## Architecture

```
GraphQL Client (GraphiQL, curl, JS app)
        │
        ▼
┌─────────────────────────────────┐
│  GraphQL API Layer              │
│  ┌────────────┐ ┌─────────────┐ │
│  │RuleQueryApi│ │StatsQueryApi│ │
│  └─────┬──────┘ └──────┬──────┘ │
│  ┌─────┴──────────────┐│        │
│  │ImpactQueryApi      ││        │
│  └─────┬──────────────┘│        │
├────────┼───────────────┼────────┤
│  Service Layer         │        │
│  ┌─────┴──────┐ ┌──────┴──────┐ │
│  │RuleMetadata│ │RuleStats    │ │
│  │Service     │ │Service      │ │
│  └─────┬──────┘ └──────┬──────┘ │
│  ┌─────┴──────────────┐│        │
│  │ImpactAnalysis      ││        │
│  │Service             ││        │
│  └─────┬──────────────┘│        │
├────────┼───────────────┼────────┤
│  Drools Core APIs      │        │
│  KieBase  MXBean  ImpactAnalysis│
└─────────────────────────────────┘
```

## Features

### Rule Metadata Queries
- List all packages with their rules, queries, fact types, and functions
- Get a specific rule by package and name, with all metadata attributes
- Search rules by name pattern (case-insensitive)
- Count total rules across all packages

### Execution Statistics Queries
- Session-level aggregate stats (total matches fired/created/cancelled, firing time)
- Per-rule stats (matches fired, created, cancelled, firing time, average)
- Top N rules by firing count or total firing time (for performance analysis)
- Reset all statistics via mutation

### Impact Analysis Queries
- Forward analysis: which rules are impacted if a given rule changes
- Backward analysis: which rules impact a given rule
- Reactivity types: POSITIVE, NEGATIVE, UNKNOWN
- List all rules available for analysis

## GraphQL Queries

### Query Rule Definitions

```graphql
# List all packages with their rules
{
  packages {
    name
    rules {
      name
      packageName
      loadOrder
      metadata { key value }
    }
    queryNames
    factTypeNames
    functionNames
  }
}

# Get a specific rule
{
  rule(packageName: "com.example.fraud", ruleName: "Flag Large Transaction") {
    name
    packageName
    loadOrder
    metadata { key value }
  }
}

# Search rules by name
{
  searchRules(namePattern: "fraud") {
    name
    packageName
  }
}

# Total rule count
{
  totalRuleCount
}
```

### Query Execution Statistics

```graphql
# Session-level stats
{
  sessionStats {
    sessionName
    kieBaseId
    totalMatchesFired
    totalMatchesCancelled
    totalMatchesCreated
    totalFiringTimeMs
    averageFiringTimeMs
    totalSessions
  }
}

# Stats for a specific rule
{
  ruleStats(ruleName: "Validate Order") {
    ruleName
    matchesFired
    matchesCreated
    matchesCancelled
    firingTimeMs
    averageFiringTimeMs
  }
}

# Top 5 most-fired rules
{
  topRulesByFiringCount(limit: 5) {
    ruleName
    matchesFired
    averageFiringTimeMs
  }
}

# Top 5 slowest rules
{
  topRulesByFiringTime(limit: 5) {
    ruleName
    firingTimeMs
    matchesFired
    averageFiringTimeMs
  }
}

# Reset all statistics
mutation {
  resetStats
}
```

### Query Impact Analysis

```graphql
# Full impact analysis for a rule
{
  impactAnalysis(ruleName: "Calculate Discount") {
    targetRule
    targetPackage
    totalImpacted
    totalImpacting
    impactedRules {
      ruleName
      packageName
      reactivityType
    }
    impactingRules {
      ruleName
      packageName
      reactivityType
    }
  }
}

# List all rules available for analysis
{
  analyzedRules
}
```

## Usage in Quarkus

### 1. Add dependencies to pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.drools</groupId>
        <artifactId>drools-graphql</artifactId>
    </dependency>
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-smallrye-graphql</artifactId>
    </dependency>
</dependencies>
```

### 2. Produce the services as CDI beans

```java
@ApplicationScoped
public class DroolsGraphQLProducer {

    @Inject
    KieRuntimeBuilder kieRuntimeBuilder;

    @Produces
    @ApplicationScoped
    public RuleMetadataService ruleMetadataService() {
        KieBase kieBase = kieRuntimeBuilder.newKieSession().getKieBase();
        return new RuleMetadataService(kieBase);
    }

    @Produces
    @ApplicationScoped
    public RuleStatsService ruleStatsService() {
        // Get the MXBean from the KIE session
        GenericKieSessionMonitoringMXBean mxBean = ...;
        return new RuleStatsService(mxBean);
    }

    @Produces
    @ApplicationScoped
    public ImpactAnalysisService impactAnalysisService() {
        String drl = "...";  // Load DRL sources
        return new ImpactAnalysisService(drl);
    }
}
```

### 3. Access via GraphQL UI

Start your Quarkus app and navigate to:
- **GraphQL UI**: `http://localhost:8080/q/graphql-ui`
- **GraphQL endpoint**: `http://localhost:8080/graphql`

### 4. Query via curl

```bash
# Query all rules
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ packages { name rules { name metadata { key value } } } }"}'

# Impact analysis
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ impactAnalysis(ruleName: \"My Rule\") { totalImpacted impactedRules { ruleName reactivityType } } }"}'
```

## Standalone Usage (Without Quarkus)

The service layer is framework-agnostic and can be used directly:

```java
// Rule metadata
KieBase kieBase = kieContainer.getKieBase();
RuleMetadataService metadataService = new RuleMetadataService(kieBase);

List<PackageInfo> packages = metadataService.getAllPackages();
RuleInfo rule = metadataService.getRule("com.example", "My Rule");
List<RuleInfo> found = metadataService.searchRules("fraud");
long total = metadataService.getTotalRuleCount();

// Execution statistics
GenericKieSessionMonitoringMXBean monitoring = ...; // from DroolsManagementAgent
RuleStatsService statsService = new RuleStatsService(monitoring);

SessionStats session = statsService.getSessionStats();
RuleStats ruleStats = statsService.getStatsForRule("My Rule");
List<RuleStats> hotRules = statsService.getTopRulesByFiringCount(10);

// Impact analysis
ImpactAnalysisService impactService = new ImpactAnalysisService(drlSource1, drlSource2);
ImpactAnalysisReport report = impactService.analyze("Target Rule");
report.getImpactedRules().forEach(r ->
    System.out.printf("  %s (%s)%n", r.getRuleName(), r.getReactivityType()));
```

## Module Structure

```
drools-graphql/
└── src/main/java/org/drools/graphql/
    ├── api/                          (GraphQL resolvers)
    │   ├── RuleQueryApi.java         @GraphQLApi: packages, rules, search
    │   ├── StatsQueryApi.java        @GraphQLApi: session stats, rule stats, top N
    │   └── ImpactQueryApi.java       @GraphQLApi: impact analysis
    ├── service/                      (Framework-agnostic service layer)
    │   ├── RuleMetadataService.java  Wraps KieBase for rule introspection
    │   ├── RuleStatsService.java     Wraps MXBean for execution statistics
    │   └── ImpactAnalysisService.java Wraps drools-impact-analysis
    └── dto/                          (GraphQL types / DTOs)
        ├── PackageInfo.java          Package with rules, queries, fact types
        ├── RuleInfo.java             Rule name, package, metadata, load order
        ├── MetaEntry.java            Key-value metadata attribute
        ├── SessionStats.java         Aggregate session statistics
        ├── RuleStats.java            Per-rule execution statistics
        ├── ImpactAnalysisReport.java Forward + backward impact report
        └── ImpactedRuleInfo.java     Impacted rule with reactivity type
```

## Tests

| Test Class | Tests | Coverage |
|-----------|-------|----------|
| `RuleInfoTest` | 2 | DTO conversion from Rule, null metadata |
| `RuleMetadataServiceTest` | 10 | All packages, by name, search, count, missing |
| `RuleStatsServiceTest` | 7 | Session stats, rule stats, top N, reset |
| **Total** | **19** | |
