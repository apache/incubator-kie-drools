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
package org.jbpm.ruleflow.core;

import java.util.UUID;

import org.kie.api.definition.process.WorkflowElementIdentifier;

public class WorkflowElementIdentifierFactory implements WorkflowElementIdentifier {

    private String elementId;

    public static WorkflowElementIdentifier fromExternalFormat(Long elementId) {
        return fromExternalFormat(String.valueOf(elementId));
    }

    public static WorkflowElementIdentifier fromExternalFormat(String elementId) {
        return new WorkflowElementIdentifierFactory(elementId);
    }

    public static WorkflowElementIdentifier newRandom() {
        return new WorkflowElementIdentifierFactory("jbpm-" + UUID.randomUUID().toString());
    }

    public WorkflowElementIdentifierFactory(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public <T> T toValue() {
        return (T) elementId;
    }

    @Override
    public String toString() {
        return "[uuid=" + elementId + "]";
    }

    @Override
    public int hashCode() {
        return elementId.hashCode();
    }

    @Override
    public String toExternalFormat() {
        return this.elementId;
    }

    @Override
    public int compareTo(WorkflowElementIdentifier o) {
        return this.elementId.compareTo(((WorkflowElementIdentifierFactory) o).elementId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkflowElementIdentifierFactory other = (WorkflowElementIdentifierFactory) obj;
        return elementId.equals(other.elementId);
    }

    @Override
    public String toSanitizeString() {
        return toExternalFormat().replaceAll("-", "_");
    }

}
