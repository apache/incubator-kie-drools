PMML Compiler
-------------

PMML Compiler uses Java SPI to retrieve AlgorithmImplementationProvider available at runtime.

To test it:

1. cd kie-pmml-library
2. mvn clean package
3. java -cp ./kie-pmml-api/target/kie-pmml-api-7.32.0-SNAPSHOT.jar:./kie-pmml-marshaller/target/kie-pmml-marshaller-7.32.0-SNAPSHOT.jar:./kie-pmml-compiler/target/kie-pmml-compiler-7.32.0-SNAPSHOT.jar org.kie.pmml.compiler.Main (No provider found expected)
4. java -cp ./kie-pmml-api/target/kie-pmml-api-7.32.0-SNAPSHOT.jar:./kie-pmml-marshaller/target/kie-pmml-marshaller-7.32.0-SNAPSHOT.jar:./kie-pmml-regression/target/kie-pmml-regression-7.32.0-SNAPSHOT.jar:./kie-pmml-compiler/target/kie-pmml-compiler-7.32.0-SNAPSHOT.jar org.kie.pmml.compiler.Main (Expected Regression provider found)
