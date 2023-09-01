package org.drools.model.codegen.project;

public class MissingDecisionTableDependencyError extends Error {

    public MissingDecisionTableDependencyError() {
        super("A Decision Table resource was found, but a necessary dependency is missing. \n" +
                "Verify that you have the drools bom in your dependencyManagement:\n" +
                "\n" +
                "<dependencyManagement>" +
                "    <dependency>\n" +
                "        <groupId>org.drools</groupId>\n" +
                "        <artifactId>drools-bom</artifactId>\n" +
                "        <type>pom</type>\n" +
                "        <scope>import</scope>\n" +
                "    </dependency>\n" +
                "</dependencyManagement>" +
                "\n" +
                "and added decision table support to your project dependencies: \n" +
                "\n" +
                "<dependency>\n" +
                "    <groupId>org.drools</groupId>\n" +
                "    <artifactId>drools-decisiontables</artifactId>\n" +
                "</dependency>");
    }
}
