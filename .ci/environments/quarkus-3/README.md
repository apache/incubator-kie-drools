# quarkus-3 environment scripts

This folder contains specific script(s)/patch(es) for the Quarkus 3 migration.

**Here is the command:**

```bash
.ci/environments/update.sh quarkus-3
```

Steps of the basic execution:

- Execute `before.sh` script  
  In the basic execution, this script will skip the rewrite commands, which is taking a lot of time to perform.  
  If you want to perform the rewrite or update the "before.sh" patch, please see next sections.
- Apply all patches from `patches` folder

## Full execution

**Command:**

```bash
.ci/environments/update.sh quarkus-3 rewrite
```

Steps of the full execution:

- Execute the rewrite execution
- Synchronize the libraries' version with Quarkus BOM
- Store the changes from previous steps into the `patches/001_before_sh.patch` file
- Apply patches from `patches` folder

## Patches information

1. the `0001_before_sh.patch` is generated executing the `before.sh` script; it then contains all the `openrewrite` migration and the synchronization of libraries with quarkus ones (see next section)
2. all other patches have been made manually
3. if some other modifications are needed, they should be created as `patch`, following numerations
4. if some patch does not apply anymore, it has to be recreated manually; in case of the first one, it means to execute the `before.sh` script again

## How to recreate the `001_before_sh.patch` file ?

The `001_before_sh.patch` file contains all changes from a rewrite execution.  
In case of a full execution, this file will be overriden with the new changes.

You can also regenerate that file without having to run the full quarkus-3 environment migration.  
To do so, just run:

```bash
.ci/environments/quarkus-3/before.sh rewrite
```

## Recipe files

There are 3 recipe files:

- `project-recipe.yml` is the recipe file to update in case you need a new recipe
- `quarkus3-base-recipe.yml` is the base recipe setup by Quarkus team in https://github.com/quarkusio/quarkus-updates. You should not modify it !
- `quarkus3.yml` is the final recipe file and is a compute of the previous 2 files, plus some processing.  
  See also comments in [Jbang script](jbang/CreateKieQuarkusProjectMigrationRecipe.java) for more details on the generation.

### How to reset the quarkus3.yaml recipe file ?

The `before.sh` script should handle the reset of the `quarkus3.yml` recipe file when executed with `rewrite` command.

In case you want to do manually, just run:

```bash
cd .ci/environments/quarkus-3 && curl -Ls https://sh.jbang.dev | bash -s - jbang/CreateKieQuarkusProjectMigrationRecipe.java; cd -
```
  
### How to update the Quarkus version ?

If you are setting a new Quarkus version:

1. Update `quarkus-devtools-common` version in `jbang/CreateKieQuarkusProjectMigrationRecipe.java` file
2. Update `QUARKUS_VERSION` in `jbang/CreateKieQuarkusProjectMigrationRecipe.java` file
3. Update `QUARKUS_UPDATES_BASE_URL` with the corresponding released version of https://github.com/quarkusio/quarkus-updates recipe file
4. Run the jbang script to update the `quarkus3.yml` file
  ```bash
  cd .ci/environments/quarkus-3 && curl -Ls https://sh.jbang.dev | bash -s - jbang/CreateKieQuarkusProjectMigrationRecipe.java true; cd -
  ```
