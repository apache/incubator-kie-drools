Notes on compilation/runtime phases relationship
================================================

1. Common code/informations from compile time to runtime
    1. informations needed at runtime, common for all the models, must be put inside code-generated model-specific KiePMMLModel, as it currently happen for KiePMMLRegressionModel (e.g. Transformation dictionary)
    2. whenever possible, generated code should contain model-specific data, to limit as much as possible runtime computation (again, see KiePMMLRegressionModel)
2. Compilation
    1. the PMMLCompiler should be invocable by  both the PMMLAssembler (at runtime) and the kie maven plugin (during kjar creation)
    2. for drools-related models, the PMMLCompiler should generate the java classes (currently, it stop at PackageDescr creation)
    3. for internal use, model' unique identifier (name) will be the full path of the pmml file
    4. for drools-related models, a kie module xml descriptor will be provided to identify kie-bases with specific model (one kie-base for each pmml file); name of such kie-bases will be the above unique identifier
    5. for drools-unrelated models, the PMMLCompiler should generate a Factory class to instantiate (at runtime) the other generated classes
3. Runtime
    1. Start time
        1. the PMMLAssembler should verify if, for any given PMML file, there are the corresponding generated classes (being them pure-java code-generated or drools-generated classes)
        2. if generated classes are not found, the PMMLAssembler must invoke the PMMLCompiler to generate them (see 2)
    2. Evaluation time (on user input)
        1. Common
            1. the input data must contain the name of the model (see 2.iii) - full path of the pmml file) to be used as parameter to retrieve the model-specific entry point from the (generated) factory method (model-specific KiePMMLModel must be instantiated to retrieve informations created/stored at 1.i)
        2. Drools-implemented models
            1. the name of the model (3.ii.a.a) will represent the kie-base name (see 2.iv) and will be passed as parameter to KiePMMLSessionUtils.builder to instantiate a kie-session specific for such kie-base, so that rules from different pmml models does not get mixed;
