# Kogito Quarkus BOM

In this module you will find the `kogito-quarkus-bom`
BOM ([Bill of Materials](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms))
.

This BOM goal is to include all Kogito Core and Quarkus specific dependencies in one single file.

> **Note:** Users are **not** encouraged to use this BOM. Rather use [`kogito-bom`](../../kogito-bom) with the [Quarkus BOM]() and [Kogito Quarkus extensions](../extensions).

## Adding new dependencies

When a new dependency is needed only by Quarkus modules add it directly here instead
of [`kogito-dependencies-bom`](../../kogito-build/kogito-dependencies-bom).

Use the same approach you would for adding a new dependency to Kogito Build Parent
by [following our guidelines](../../CONTRIBUTING.md#requirements-for-dependencies).
