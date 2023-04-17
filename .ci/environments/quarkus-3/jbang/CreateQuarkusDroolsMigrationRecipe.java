import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.quarkus.devtools.project.BuildTool;
import io.quarkus.devtools.project.update.QuarkusUpdateRecipe;
import io.quarkus.devtools.project.update.QuarkusUpdateRecipeIO;
import io.quarkus.devtools.project.update.operations.UpdatePropertyOperation;

///usr/bin/env jbang "$0" "$@" ; exit $?
// Version to be changed when needed
//DEPS io.quarkus:quarkus-devtools-common:3.0.0.Final

/*
 * This script will generate the final `quarkus3.yml` file based on:
 *   - quarkus recipe file (see `QUARKUS_UPDATES_BASE_URL` constant)
 *   - local drools-recipe.yaml => Specific drools repository rules
 * 
 * As in Drools we use a lot of managed dependencies, it concatenates both files but it also add some new rules:
 * In the Quarkus recipe, the dependencies rules are modified only for direct dependencies but not for managed dependencies.
 * So the script adds a new step:
 *   - Reads all modified direct dependencies from the Quarkus recipe
 *   - Generates one managed dependency rule for each of them
 */
class CreateQuarkusDroolsMigrationRecipe {

    static final String QUARKUS_VERSION = "3.0.0.Final";
    static final String QUARKUS_UPDATES_BASE_URL = "https://raw.githubusercontent.com/quarkusio/quarkus-updates/1.0.0/recipes/src/main/resources/quarkus-updates/core/3alpha.yaml";

    static final Path quarkus3GeneratedRecipePath = Paths.get("quarkus3.yml");
    static final Path droolsBaseRecipePath = Paths.get("drools-recipe.yml");

    public static void main(String... args) throws Exception {
        List<Object> quarkusRecipes = QuarkusUpdateRecipeIO.readRecipesYaml(IOUtils.toString(new URL(QUARKUS_UPDATES_BASE_URL).openStream(), Charset.defaultCharset()));
        QuarkusUpdateRecipe mainRecipe = new QuarkusUpdateRecipe()
                .buildTool(BuildTool.MAVEN)
                .addOperation(new UpdatePropertyOperation("version.io.quarkus", QUARKUS_VERSION));
        
        System.out.println("Adding Drools base recipe(s)");
        mainRecipe.addRecipes(QuarkusUpdateRecipeIO.readRecipesYaml(Files.readString(droolsBaseRecipePath)));

        System.out.println("Adding Managed dependency recipe(s)");
        Map<String, Object> managedDependencyMainRecipe = Map.of(
            "type", "specs.openrewrite.org/v1beta/recipe",
            "name", "org.kie.ManagedDependencies",
            "displayName", "Update Managed Dependencies",
            "description", "Update all managed dependencies based on dependency updates from Quarkus.",
            "recipeList", retrieveAllChangeDependencyRecipesToManagedDependency(quarkusRecipes)
        );
        mainRecipe.addRecipe(managedDependencyMainRecipe);
        
        System.out.println("Adding Quarkus base recipe(s)");
        mainRecipe.addRecipes(quarkusRecipes);

        System.out.println("Writing main recipe");
        QuarkusUpdateRecipeIO.write(quarkus3GeneratedRecipePath, mainRecipe);
    }

    private static List<Object> retrieveAllChangeDependencyRecipesToManagedDependency(List<Object> recipes) {
        List<Object> changeDependencyRecipeList = new ArrayList<>();
        recipes.forEach(r -> {
            if (r instanceof Map) {
                List<Object> recipeList = (List<Object>) ((Map<String, Object>) r).get("recipeList");
                recipeList.forEach(recipeMap -> {
                    if (recipeMap instanceof Map) {
                        ((Map<String, Map<String, Object>>) recipeMap).forEach((recipeName, args) -> {
                            if (recipeName.equals("org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId")) {
                                args.remove("overrideManagedVersion");
                                if (!args.containsKey("newArtifactId")) {
                                    args.put("newArtifactId", args.get("oldArtifactId"));
                                }
                                changeDependencyRecipeList.add(Map.of("org.openrewrite.maven.ChangeManagedDependencyGroupIdAndArtifactId", args));
                            }
                        });
                    }
                });
            }
        });
        return changeDependencyRecipeList;
    }
}
