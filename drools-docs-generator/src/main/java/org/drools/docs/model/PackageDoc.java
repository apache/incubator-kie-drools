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
 * Documentation for a DRL/YAML package containing rules, globals, imports, etc.
 */
public class PackageDoc {

    public enum SourceFormat { DRL, YAML_DRL }

    private String name;
    private SourceFormat sourceFormat;
    private String sourceFile;
    private String documentation;
    private final List<String> imports = new ArrayList<>();
    private final List<GlobalDoc> globals = new ArrayList<>();
    private final List<RuleDoc> rules = new ArrayList<>();
    private final List<TypeDeclarationDoc> typeDeclarations = new ArrayList<>();
    private final List<FunctionDoc> functions = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SourceFormat getSourceFormat() {
        return sourceFormat;
    }

    public void setSourceFormat(SourceFormat sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<GlobalDoc> getGlobals() {
        return globals;
    }

    public List<RuleDoc> getRules() {
        return rules;
    }

    public List<TypeDeclarationDoc> getTypeDeclarations() {
        return typeDeclarations;
    }

    public List<FunctionDoc> getFunctions() {
        return functions;
    }
}
