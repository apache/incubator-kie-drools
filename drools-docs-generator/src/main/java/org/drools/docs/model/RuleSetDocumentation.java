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

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Top-level documentation model aggregating all parsed rule sources.
 */
public class RuleSetDocumentation {

    private String title;
    private String description;
    private LocalDateTime generatedAt;
    private final List<PackageDoc> packages = new ArrayList<>();
    private final List<DecisionModelDoc> decisionModels = new ArrayList<>();
    private final List<YardDoc> yardDefinitions = new ArrayList<>();
    private final List<Path> sourceFiles = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<PackageDoc> getPackages() {
        return packages;
    }

    public List<DecisionModelDoc> getDecisionModels() {
        return decisionModels;
    }

    public List<YardDoc> getYardDefinitions() {
        return yardDefinitions;
    }

    public List<Path> getSourceFiles() {
        return sourceFiles;
    }
}
