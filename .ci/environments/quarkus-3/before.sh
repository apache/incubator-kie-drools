#!/bin/bash
set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
ci="${CI:-false}"

rewrite_plugin_version=4.43.0
quarkus_version=${QUARKUS_VERSION:-3.0.0.Final}

quarkus_recipe_file="${script_dir_path}/quarkus3.yml"
patch_file="${script_dir_path}"/patches/0001_before_sh.patch

if [ "${ci}" = "true" ]; then
    # In CI we need the main branch snapshot artifacts deployed locally
    set -x
    ${mvn_cmd} clean install -Dquickly
    set +x
fi

rewrite=${1:-'none'}
behavior=${2:-'none'}
echo "rewrite "${rewrite}
if [ "rewrite" != ${rewrite} ]; then
    echo "No rewrite to be done. Exited"
    exit 0
fi

export MAVEN_OPTS="-Xmx16192m"

echo "Update project with Quarkus version ${quarkus_version}"

set -x

project_version=$(${mvn_cmd} -q -Dexpression=project.version -DforceStdout help:evaluate)

# Regenerate quarkus3 recipe
cd ${script_dir_path}
curl -Ls https://sh.jbang.dev | \
    bash -s - jbang/CreateKieQuarkusProjectMigrationRecipe.java \
        -v version.io.quarkus=${quarkus_version} 
cd -

# Launch Quarkus 3 Openrewrite
${mvn_cmd} org.openrewrite.maven:rewrite-maven-plugin:${rewrite_plugin_version}:run \
    -Drewrite.configLocation="${quarkus_recipe_file}" \
    -DactiveRecipes=io.quarkus.openrewrite.Quarkus \
    -Drewrite.recipeArtifactCoordinates=org.kie:jpmml-migration-recipe:"${project_version}" \
    -Denforcer.skip \
    -fae \
    -Dexclusions=**/target \
    -DplainTextMasks=**/kmodule.xml

# Update dependencies with Quarkus 3 bom
${mvn_cmd} \
    -pl :drools-build-parent \
    -DremotePom=io.quarkus:quarkus-bom:${quarkus_version} \
    -DupdatePropertyVersions=true \
    -DupdateDependencies=true \
    -DgenerateBackupPoms=false \
    versions:compare-dependencies

# Create the `patches/0001_before_sh.patch` file
git add .
git reset "${quarkus_recipe_file}" # Do not include recipe file
git diff --cached > "${patch_file}"
git reset

# Commit the change on patch
if [ "$(git status --porcelain ${patch_file})" != '' ]; then
    if [ "$(git status --porcelain ${quarkus_recipe_file})" != '' ]; then
        git add "${quarkus_recipe_file}" # We suppose that if the recipe has changed, the patch file as well
    fi
    git add "${patch_file}"
    git commit -m '[Quarkus 3] Updated rewrite data'

    git reset --hard
    if [ "${behavior}" = 'push_changes' ]; then
        git_remote="${GIT_REMOTE:-origin}"
        branch=$(git branch --show-current)
        echo "Pushing changes to ${git_remote}/${branch} after rebase "
        git fetch ${git_remote}
        git rebase ${git_remote}/${branch}
        git push ${git_remote} ${branch}
    fi
fi

# Reset all other changes as they will be applied next by the `patches/0001_before_sh.patch` file
git reset --hard
