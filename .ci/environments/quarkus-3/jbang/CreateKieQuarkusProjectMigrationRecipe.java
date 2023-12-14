import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import io.quarkus.devtools.project.BuildTool;
import io.quarkus.devtools.project.update.rewrite.QuarkusUpdateRecipe;
import io.quarkus.devtools.project.update.rewrite.QuarkusUpdateRecipeIO;
import io.quarkus.devtools.project.update.rewrite.operations.UpdatePropertyOperation;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

///usr/bin/env jbang "$0" "$@" ; exit $?
// Version to be changed when needed
//DEPS io.quarkus:quarkus-devtools-common:3.2.9.Final
//DEPS info.picocli:picocli:4.5.0

/*
 * This script will generate the final `quarkus3.yml` file based on:
 *   - quarkus recipe file (see `QUARKUS_UPDATES_BASE_URL` constant)
 *   - local project-recipe.yaml => Specific project repository rules
 * 
 * We use a lot of managed dependencies, it concatenates both files but it also add some new rules:
 * In the Quarkus recipe, the dependencies rules are modified only for direct dependencies but not for managed dependencies.
 * So the script adds a new step:
 *   - Reads all modified direct dependencies from the Quarkus recipe
 *   - Generates one managed dependency rule for each of them
 */
@Command(name = "migrationrecipecli", mixinStandardHelpOptions = true, version = "migrationrecipecli 0.1",
        description = "migrationrecipecli to create the Q3 migration recipe for a project")
 class CreateKieQuarkusProjectMigrationRecipeCli implements Callable<Integer> {

    @Option(names={ "-d", "--download-quarkus-recipe"}, description = "Download quarkus update recipe for final recipe generation")
    private boolean downloadQuarkusRecipe = false;

    @Option(names={ "-v", "--property-version"}, description = "(multi). Add a dynamic property version to the final recipe")
    private Map<String, String> versionProperties = new HashMap<>();

    static final String QUARKUS_UPDATES_BASE_URL = "https://github.com/quarkusio/quarkus-updates/blob/main/recipes/src/main/resources/quarkus-updates/core/3.2.yaml";

    static final Path quarkus3DownloadedRecipePath = Paths.get("quarkus3-base-recipe.yml");
    static final Path quarkus3GeneratedRecipePath = Paths.get("quarkus3.yml");
    static final Path projectBaseRecipePath = Paths.get("project-recipe.yml");

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        if (downloadQuarkusRecipe) {
            System.out.println("Downloading recipe from Quarkus");
            Files.write(quarkus3DownloadedRecipePath, new URL(QUARKUS_UPDATES_BASE_URL).openStream().readAllBytes());
        }

        if (!Files.exists(quarkus3DownloadedRecipePath)) {
            System.out.println("The Quarkus base recipe (" + quarkus3DownloadedRecipePath.getFileName()
                    + ") does not exist into the folder. Please download it manually or add the `true` parameter to the script call !");
            return 1;
        }

        List<Object> quarkusRecipes = QuarkusUpdateRecipeIO
                .readRecipesYaml(Files.readString(quarkus3DownloadedRecipePath));
        QuarkusUpdateRecipe mainRecipe = new QuarkusUpdateRecipe()
                .buildTool(BuildTool.MAVEN);
        versionProperties.forEach((property, version) -> {
            System.out.println("Add Property '" + property + "' with value '" + version + "'");
            mainRecipe.addOperation(new UpdatePropertyOperation(property, version));
        });

        if (Files.exists(projectBaseRecipePath)) {
            System.out.println("Adding Project base recipe(s)");
            mainRecipe.addRecipes(QuarkusUpdateRecipeIO.readRecipesYaml(Files.readString(projectBaseRecipePath)));
        } else {
            System.out.println("No Project base recipe(s) available. Nothing done here ...");
        }

        System.out.println("Adding Managed dependency recipe(s)");
        Map<String, Object> managedDependencyMainRecipe = Map.of(
                "type", "specs.openrewrite.org/v1beta/recipe",
                "name", "org.kie.ManagedDependencies",
                "displayName", "Update Managed Dependencies",
                "description", "Update all managed dependencies based on dependency updates from Quarkus.",
                "recipeList", retrieveAllChangeDependencyRecipesToManagedDependency(quarkusRecipes));
        mainRecipe.addRecipe(managedDependencyMainRecipe);

        System.out.println("Adding Quarkus base recipe(s)");
        mainRecipe.addRecipes(quarkusRecipes);

        System.out.println("Writing main recipe");
        QuarkusUpdateRecipeIO.write(quarkus3GeneratedRecipePath, mainRecipe);

        return 0;
    }


    public static void main(String... args) throws Exception {
        int exitCode = new CommandLine(new CreateKieQuarkusProjectMigrationRecipeCli()).execute(args);
        System.exit(exitCode);
    }

    private List<Object> retrieveAllChangeDependencyRecipesToManagedDependency(List<Object> recipes) {
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
                                changeDependencyRecipeList.add(Map
                                        .of("org.openrewrite.maven.ChangeManagedDependencyGroupIdAndArtifactId", args));
                            }
                        });
                    }
                });
            }
        });
        return changeDependencyRecipeList;
    }
}
