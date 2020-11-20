/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.svg.dataindex;

import java.util.Objects;

public class NodeInstance {

    private Boolean completed;
    private String definitionId;

    public NodeInstance(Boolean completed, String definitionId) {
        this.completed = completed;
        this.definitionId = definitionId;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeInstance)) {
            return false;
        }
        NodeInstance that = (NodeInstance) o;
        return completed.equals(that.completed) &&
                getDefinitionId().equals(that.getDefinitionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(completed, getDefinitionId());
    }

    @Override
    public String toString() {
        return "NodeInstance{" +
                "completed=" + completed +
                ", definitionId='" + definitionId + '\'' +
                '}';
    }
}
