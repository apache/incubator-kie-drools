Models Archetype
================

Usage:

    cd drools/kie-pmml-trusty/kie-pmml-models/kie-pmml-models-drools

    mvn archetype:generate -B \ (not interactive)
    -DarchetypeGroupId=org.kie \
    -DarchetypeArtifactId=kie-pmml-models-drools-archetype \
    -DarchetypeVersion=(current_kie_version) \
    -DmodelName=(model name, first letter capital) \ e.g. Testing

N.B.

For model name do follow names as defined inside PMML specs, without trailing "Model": e.g RegressionModel -> Regression, TreeModel -> Tree, ClusteringModel -> Clustering

    mvn archetype:generate -B -DarchetypeGroupId=org.kie -DarchetypeArtifactId=kie-pmml-models-drools-archetype -DarchetypeVersion=%KIE_VERSION% -DmodelName=NaiveBayes

Test:

The src/test/resources/projects contains two folder to have IT tests of the archetype itself.

Inside such folder there is
1. some specifications for the project to be created (archetype.pom.properties, archetype.properties)
2. a "reference" project, to compare the generated-one with

During "archetype:integration-test" goal, what happen is

1. a project is generated based on the given specifications
2. the generated project is compared (file by file) with the reference one
3. if the comparison is successful, the generated project goes through the phase describe inside "goal.txt"
4. Being the above "verify", the project is compiled and tested.

If reference and generated project differs for number or content of files, a specific error is printed out in console
If the generated project does not compile (due to some modification in the underlying models) a usual "compilation error" is printed out in console.

See https://maven.apache.org/archetype/maven-archetype-plugin/integration-test-mojo.html for more details.

Post-creation steps
-------------------

The following overriding methods are not currently implemented, and will throw _UnsupportedOperationException_ if invoked:

1. KiePMML(_modelName_)ModelFactory.getKiePMML(_modelName_)Model(DataDictionary dataDictionary, (_modelName_)Model model)
2. KiePMML(_modelName_)Model.evaluate(Map<String, Object> requestData)

Also, some _dependencyManagement_ declarations are defined inside generated pom, and must be moved to _kie-pmml-trusty/pom.xml_

1. kie-pmml-models-(_modelName_)-compiler/pom.xml
1. kie-pmml-models-(_modelName_)-evaluator/pom.xml



