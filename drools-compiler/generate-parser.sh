#/bin/bash

java -classpath "lib/antlr-3.0.1.jar;lib/stringtemplate-3.1b1.jar;lib/antlrworks-1.2.1.jar" org.antlr.Tool -lib src/main/resources/org/drools/lang  src/main/resources/org/drools/lang/DRL.g src/main/resources/org/drools/lang/DescrBuilderTree.g

mv src/main/resources/org/drools/lang/*.java src/main/java/org/drools/lang/

rm src/main/resources/org/drools/lang/*.tokens src/main/resources/org/drools/lang/DRL__.g
