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

## How reset the quarkus3.yaml recipe file ?

The `quarkus3.yml` file is generated automatically based on:

- https://github.com/quarkusio/quarkus-updates/blob/main/recipes/src/main/resources/quarkus-updates/core/3alpha.yaml  
  This file is the Quarkus base recipe for Quarkus 2->3 migration
- `drools_recipe.yml` file  
  This file contains all other simple modifications needed for the Quarkus 2->3 migration on Drools project
  
To refresh the recipe file, just execute:

```bash
cd .ci/environments/quarkus-3
curl -Ls https://sh.jbang.dev | bash -s - jbang/CreateQuarkusDroolsMigrationRecipe.java
cd -
```

If you are setting a new quarkus version:

1. Update `quarkus-devtools-common` version in `jbang/CreateQuarkusDroolsMigrationRecipe.java` file
2. Update `QUARKUS_VERSION` in `jbang/CreateQuarkusDroolsMigrationRecipe.java` file
3. Update the `QUARKUS_UPDATES_BASE_URL` with the corresponding released version of https://github.com/quarkusio/quarkus-updates
4. Rerun the jbang script (see above)
