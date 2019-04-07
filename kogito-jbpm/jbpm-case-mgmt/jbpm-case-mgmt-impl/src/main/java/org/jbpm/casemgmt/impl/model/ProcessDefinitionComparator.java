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

package org.jbpm.casemgmt.impl.model;

import java.util.Comparator;

import org.jbpm.services.api.model.ProcessDefinition;

/**
 * Comparator used for sorting of ProcessDefinitions - currently by:
 * <ul>
 *  <li>name - gives as name</li>
 *  <li>id - gives as processId</li>
 *  <li>deploymentId - gives as Project</li>
 * </ul>
 */
public class ProcessDefinitionComparator implements Comparator<ProcessDefinition> {
    
    private String orderBy;
    private boolean ascending;
    
    public ProcessDefinitionComparator(String orderBy, Boolean ascending) {
        this.orderBy = orderBy;
        this.ascending = ascending == null ? Boolean.TRUE : ascending;
    }

    @Override
    public int compare(ProcessDefinition o1, ProcessDefinition o2) {
        int result = 0;

        if ("CaseName".equals(orderBy)) {
            result = o1.getName().compareTo(o2.getName());
        } else if ("CaseId".equals(orderBy)) {
            result = o1.getId().compareTo(o2.getId());
        } else if ("Project".equals(orderBy)) {
            result = o1.getDeploymentId().compareTo(o2.getDeploymentId());
        }

        if (!ascending) {
            result = reverseCompareResult(result);
        }

        return result;
    }

    private int reverseCompareResult(int compareResult) {
        return -compareResult;
    }
}
