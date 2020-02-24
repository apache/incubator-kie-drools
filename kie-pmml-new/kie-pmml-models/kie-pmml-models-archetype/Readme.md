Models Archetype
================

Usage:

    cd kie-pmml-models

    mvn archetype:generate -B \ (not interactive)
    -DarchetypeGroupId=org.kie \
    -DarchetypeArtifactId=kie-pmml-models-archetype \
    -DarchetypeVersion=(current_kie_version) \
    -DmodelName=(model name, first letter capital) \ e.g. Testing
    -DartifactId=kie-pmml-models-(model name lowercase) \ kie-pmml-models-testing

N.B.

For model name do follow names as defined inside PMML specs, e.g RegressionModel -> Regression, TreeModel -> Tree

    mvn archetype:generate -B -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-pmml-models-archetype -DarchetypeVersion=7.34.0-SNAPSHOT -DmodelName=NaiveBayes -DartifactId=kie-pmml-models-naivebayes




