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
 * Documentation for a YaRD (YAML Rules DSL) definition.
 */
public class YardDoc {

    private String name;
    private String specVersion;
    private String expressionLang;
    private String sourceFile;
    private final List<YardInputDoc> inputs = new ArrayList<>();
    private final List<YardElementDoc> elements = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecVersion() { return specVersion; }
    public void setSpecVersion(String specVersion) { this.specVersion = specVersion; }
    public String getExpressionLang() { return expressionLang; }
    public void setExpressionLang(String expressionLang) { this.expressionLang = expressionLang; }
    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }
    public List<YardInputDoc> getInputs() { return inputs; }
    public List<YardElementDoc> getElements() { return elements; }

    /**
     * A YaRD input declaration.
     */
    public static class YardInputDoc {
        private String name;
        private String type;

        public YardInputDoc() {}

        public YardInputDoc(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    /**
     * A YaRD decision element (decision table or literal expression).
     */
    public static class YardElementDoc {
        private String name;
        private String type;
        private String logicType;
        private String literalExpression;
        private String hitPolicy;
        private final List<String> requirements = new ArrayList<>();
        private final List<String> inputHeaders = new ArrayList<>();
        private final List<String> outputHeaders = new ArrayList<>();
        private final List<List<String>> rows = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLogicType() { return logicType; }
        public void setLogicType(String logicType) { this.logicType = logicType; }
        public String getLiteralExpression() { return literalExpression; }
        public void setLiteralExpression(String literalExpression) { this.literalExpression = literalExpression; }
        public String getHitPolicy() { return hitPolicy; }
        public void setHitPolicy(String hitPolicy) { this.hitPolicy = hitPolicy; }
        public List<String> getRequirements() { return requirements; }
        public List<String> getInputHeaders() { return inputHeaders; }
        public List<String> getOutputHeaders() { return outputHeaders; }
        public List<List<String>> getRows() { return rows; }
    }
}
