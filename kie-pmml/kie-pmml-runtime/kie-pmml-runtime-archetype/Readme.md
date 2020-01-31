Models Archetype
================

Usage:

    cd kie-pmml-runtime

    mvn archetype:generate -B \ (not interactive)
    -DarchetypeGroupId=org.drools \
    -DarchetypeArtifactId=kie-pmml-runtime-archetype \
    -DarchetypeVersion=(current_kie_version) \
    -DmodelName=(model name, first letter capital) \ e.g. Testing
    -DartifactId=kie-pmml-runtime-(model name lowercase) \ kie-pmml-runtime-testing

N.B.

For model name do follow names as defined inside PMML specs, e.g RegressionModel -> Regression, TreeModel -> Tree



