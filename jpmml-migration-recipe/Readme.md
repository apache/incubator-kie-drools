JPPML migration recipe
======================

The jpmml-recipe contains code needed to migrate jpmml from version 1.5.1 to 1.6.4.

The main `JPMMLRecipe` features the OpenRewrite recipe declarative chain-ability to re-use some already existing recipes, so that

1. It invokes `ChangeType` for classes that changed name/package, but kept the same method signature
2. It invokes `JPMMLCodeRecipe` for more fine-grained manipulation, e.g. removal of `FieldName` usage and replacement of `ScoreDistribution`; this is actually done inside `JPMMLVisitor`
3. It invokes `RemoveUnusedImports` to remove unused imports

There are three main modification steps:

1. `JPMMLVisitor`
2. `JPMMLCodeRecipe`
3. `JPMMLRecipe`

for each of which there is a specific unit test class. 
Testing of `JPMMLVisitor` is focused on very low level LST modification. 
Testing of `JPMMLCodeRecipe` is focused on the overall modifications implemented in this module.
Testing of `JPMMLRecipe` is focused on the full modifications applied by all the involved recipes. It is at this phase that the final, expected result should be evaluated.

The `CommonTestingUtilities` has been thought to be re-usable by different recipes, even if currently defined in that module

Usage
=====

To execute `JPMMLRecipe`, simply add the following snippet in the target project's pom

```xml
      <plugin>
        <groupId>org.openrewrite.maven</groupId>
        <artifactId>rewrite-maven-plugin</artifactId>
        <configuration>
          <activeRecipes>
            <activeRecipe>org.kie.openrewrite.recipe.jpmml.JPMMLRecipe</activeRecipe>
          </activeRecipes>
        </configuration>
        <dependencies>
          <dependency>
            <groupId>org.kie</groupId>
            <artifactId>jpmml-migration-recipe</artifactId>
          </dependency>
        </dependencies>
      </plugin>
```

and issue

`mvn rewrite:run`



