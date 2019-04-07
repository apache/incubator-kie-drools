/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.core.context.variable;

import java.util.List;
import java.util.Map;

import org.jbpm.workflow.core.node.DataAssociation;

public interface Mappable {

    void addInMapping(String parameterName, String variableName);
    void setInMappings(Map<String, String> inMapping);
    String getInMapping(String parameterName);
    Map<String, String> getInMappings();
    void addInAssociation(DataAssociation dataAssociation);
    List<DataAssociation> getInAssociations();
    
    void addOutMapping(String parameterName, String variableName);
    void setOutMappings(Map<String, String> outMapping);
    String getOutMapping(String parameterName);
    Map<String, String> getOutMappings();
    void addOutAssociation(DataAssociation dataAssociation);
    List<DataAssociation> getOutAssociations();

}
