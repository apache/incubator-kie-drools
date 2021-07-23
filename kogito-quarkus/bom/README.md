# Kogito Quarkus BOM

In this module you will find the `kogito-quarkus-bom`
BOM ([Bill of Materials](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms))
.

This BOM goal is to include all Kogito Core and Quarkus dependencies in one single file.

Users are encouraged to use this BOM with the [Kogito Quarkus extensions](../extensions).

## How to use it

Simply add the BOM in your module's `pom.xml` file:

```xml

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-quarkus-bom</artifactId>
      <version>${project.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

If your project has a parent module, add there instead of adding in every child module.

> **Maintainers**: relevant parent modules within the project already have it like [`addons`](../addons) and [`extensions`](../extensions).

## Adding new dependencies

When a new dependency is needed only by Quarkus modules add it directly here instead
of [`kogito-build-parent`](../../kogito-build/kogito-build-parent). But remember to keep the **properties** there. It
will be easier to update the versions having only one place to look.

Use the same approach you would for adding a new dependency to Kogito Build Parent
by [following our guidelines](../../CONTRIBUTING.md#requirements-for-dependencies).
