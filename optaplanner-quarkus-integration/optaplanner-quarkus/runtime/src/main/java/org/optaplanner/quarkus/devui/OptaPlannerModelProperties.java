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

package org.optaplanner.quarkus.devui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OptaPlannerModelProperties {
    String solutionClass;
    List<String> entityClassList;
    Map<String, List<String>> entityClassToGenuineVariableListMap;
    Map<String, List<String>> entityClassToShadowVariableListMap;

    public OptaPlannerModelProperties() {
        solutionClass = "null";
        entityClassList = Collections.emptyList();
        entityClassToGenuineVariableListMap = Collections.emptyMap();
        entityClassToShadowVariableListMap = Collections.emptyMap();
    }

    public String getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(String solutionClass) {
        this.solutionClass = solutionClass;
    }

    public List<String> getEntityClassList() {
        return entityClassList;
    }

    public void setEntityClassList(List<String> entityClassList) {
        this.entityClassList = entityClassList;
    }

    public Map<String, List<String>> getEntityClassToGenuineVariableListMap() {
        return entityClassToGenuineVariableListMap;
    }

    public void setEntityClassToGenuineVariableListMap(
            Map<String, List<String>> entityClassToGenuineVariableListMap) {
        this.entityClassToGenuineVariableListMap = entityClassToGenuineVariableListMap;
    }

    public Map<String, List<String>> getEntityClassToShadowVariableListMap() {
        return entityClassToShadowVariableListMap;
    }

    public void setEntityClassToShadowVariableListMap(
            Map<String, List<String>> entityClassToShadowVariableListMap) {
        this.entityClassToShadowVariableListMap = entityClassToShadowVariableListMap;
    }
}
