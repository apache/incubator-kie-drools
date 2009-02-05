#/bin/bash

java -classpath "$HOME/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:$HOME/.m2/repository/org/antlr/stringtemplate/3.2/stringtemplate-3.2.jar:$HOME/.m2/repository/org/antlr/antlr/3.1.1/antlr-3.1.1.jar" org.antlr.Tool -lib src/main/resources/org/drools/lang  src/main/resources/org/drools/lang/DRL.g src/main/resources/org/drools/lang/DescrBuilderTree.g

mv src/main/resources/org/drools/lang/*.java src/main/java/org/drools/lang/


java -classpath "$HOME/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:$HOME/.m2/repository/org/antlr/stringtemplate/3.2/stringtemplate-3.2.jar:$HOME/.m2/repository/org/antlr/antlr/3.1.1/antlr-3.1.1.jar" org.antlr.Tool -lib src/main/resources/org/drools/lang  src/main/resources/org/drools/lang/Tree2TestDRL.g

mv src/main/resources/org/drools/lang/*.java src/test/java/org/drools/lang/

rm src/main/resources/org/drools/lang/*.tokens src/main/resources/org/drools/lang/DRL__.g
