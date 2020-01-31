Models Archetype
================

Usage:


    mvn archetype:generate -B \ (not interactive)
    -DarchetypeGroupId=org.drools \
    -DarchetypeArtifactId=kie-pmml-models-archetype \
    -DarchetypeVersion=(current_kie_version) \
    -DmodelName=(model name, first letter capital) \ e.g. Testing
    -DartifactId=kie-pmml-models-(model name lowercase) \ kie-pmml-models-testing

N.B.

For model name do follow names as defined inside PMML specs, e.g RegressionModel -> Regression, TreeModel -> Tree



