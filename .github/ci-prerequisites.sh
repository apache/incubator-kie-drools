## This script is used by the Quarkus ecosystem (.github/workflows/quarkus-snapshot.yml)

################################################
#### Quarkus part

../ecosystem-ci/ci-prerequisites.sh

################################################

# Checkout and Install quickly Drools

current_path=$(pwd)

cd ../drools
mvn clean install -Dquickly

cd ${current_path}

