/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import java.util.HashSet;
import java.util.Set;

public class ProcessMetaData {

    private String processClassName;

    private String processId;

    private String extractedProcessId;

    private String processName;

    private String processVersion;

    private String generatedClassModel;

    private Set<String> workItems = new HashSet<>();

    public ProcessMetaData(String processId, String extractedProcessId, String processName, String processVersion, String processClassName) {
        super();
        this.processId = processId;
        this.extractedProcessId = extractedProcessId;
        this.processName = processName;
        this.processVersion = processVersion;
        this.processClassName = processClassName;
    }

    public String getProcessClassName() {
        return processClassName;
    }

    public void setProcessClassName(String processClassName) {
        this.processClassName = processClassName;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getExtractedProcessId() {
        return extractedProcessId;
    }

    public void setExtractedProcessId(String extractedProcessId) {
        this.extractedProcessId = extractedProcessId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getGeneratedClassModel() {
        return generatedClassModel;
    }

    public void setGeneratedClassModel(String generatedClassModel) {
        this.generatedClassModel = generatedClassModel;
    }

    public Set<String> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(Set<String> workItems) {
        this.workItems = workItems;
    }

    @Override
    public String toString() {
        return "ProcessMetaData [processClassName=" + processClassName + 
                ", processId=" + processId + ", extractedProcessId=" + extractedProcessId + 
                ", processName=" + processName + ", processVersion=" + processVersion +
               ", workItems=" + workItems + "]";
    }
}
