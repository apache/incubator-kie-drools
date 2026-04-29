/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.docs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Documentation for a DMN decision model.
 */
public class DecisionModelDoc {

    private String name;
    private String namespace;
    private String sourceFile;
    private final List<InputDataDoc> inputs = new ArrayList<>();
    private final List<DecisionDoc> decisions = new ArrayList<>();
    private final List<ItemDefinitionDoc> itemDefinitions = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<InputDataDoc> getInputs() {
        return inputs;
    }

    public List<DecisionDoc> getDecisions() {
        return decisions;
    }

    public List<ItemDefinitionDoc> getItemDefinitions() {
        return itemDefinitions;
    }

    /**
     * Documentation for a DMN input data element.
     */
    public static class InputDataDoc {
        private String name;
        private String typeRef;

        public InputDataDoc() {}

        public InputDataDoc(String name, String typeRef) {
            this.name = name;
            this.typeRef = typeRef;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTypeRef() { return typeRef; }
        public void setTypeRef(String typeRef) { this.typeRef = typeRef; }
    }

    /**
     * Documentation for a DMN decision element.
     */
    public static class DecisionDoc {
        private String name;
        private String question;
        private String outputTypeRef;
        private String expressionType;
        private String literalExpression;
        private DecisionTableDoc decisionTable;
        private final List<String> informationRequirements = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getOutputTypeRef() { return outputTypeRef; }
        public void setOutputTypeRef(String outputTypeRef) { this.outputTypeRef = outputTypeRef; }
        public String getExpressionType() { return expressionType; }
        public void setExpressionType(String expressionType) { this.expressionType = expressionType; }
        public String getLiteralExpression() { return literalExpression; }
        public void setLiteralExpression(String literalExpression) { this.literalExpression = literalExpression; }
        public DecisionTableDoc getDecisionTable() { return decisionTable; }
        public void setDecisionTable(DecisionTableDoc decisionTable) { this.decisionTable = decisionTable; }
        public List<String> getInformationRequirements() { return informationRequirements; }
    }

    /**
     * Documentation for a DMN decision table.
     */
    public static class DecisionTableDoc {
        private String hitPolicy;
        private final List<String> inputHeaders = new ArrayList<>();
        private final List<String> outputHeaders = new ArrayList<>();
        private final List<List<String>> rows = new ArrayList<>();

        public String getHitPolicy() { return hitPolicy; }
        public void setHitPolicy(String hitPolicy) { this.hitPolicy = hitPolicy; }
        public List<String> getInputHeaders() { return inputHeaders; }
        public List<String> getOutputHeaders() { return outputHeaders; }
        public List<List<String>> getRows() { return rows; }
    }

    /**
     * Documentation for a DMN item definition (type).
     */
    public static class ItemDefinitionDoc {
        private String name;
        private String typeRef;
        private final List<String> allowedValues = new ArrayList<>();
        private final List<ItemDefinitionDoc> components = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTypeRef() { return typeRef; }
        public void setTypeRef(String typeRef) { this.typeRef = typeRef; }
        public List<String> getAllowedValues() { return allowedValues; }
        public List<ItemDefinitionDoc> getComponents() { return components; }
    }
}
