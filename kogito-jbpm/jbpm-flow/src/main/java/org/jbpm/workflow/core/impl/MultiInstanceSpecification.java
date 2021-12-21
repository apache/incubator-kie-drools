/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workflow.core.impl;

import java.io.Serializable;

public class MultiInstanceSpecification implements Serializable {

    private static final long serialVersionUID = -608292180338609330L;

    private DataDefinition inputDataItem;
    private DataDefinition outputDataItem;
    private DataDefinition loopDataOutputRef;
    private DataDefinition loopDataInputRef;
    private String completionCondition;
    private boolean isSequential = false;

    public boolean hasMultiInstanceInput() {
        return loopDataInputRef != null;
    }

    public void setInputDataItem(DataDefinition inputDataItem) {
        this.inputDataItem = inputDataItem;
    }

    public DataDefinition getInputDataItem() {
        return inputDataItem;
    }

    public DataDefinition getOutputDataItem() {
        return outputDataItem;
    }

    public void setOutputDataItem(DataDefinition outputDataItem) {
        this.outputDataItem = outputDataItem;
    }

    public void setLoopDataOutputRef(DataDefinition loopDataOutputRef) {
        this.loopDataOutputRef = loopDataOutputRef;
    }

    public DataDefinition getLoopDataOutputRef() {
        return loopDataOutputRef;
    }

    public void setLoopDataInputRef(DataDefinition loopDataInputRef) {
        this.loopDataInputRef = loopDataInputRef;
    }

    public DataDefinition getLoopDataInputRef() {
        return loopDataInputRef;
    }

    public void setCompletionCondition(String completionCondition) {
        this.completionCondition = completionCondition;
    }

    public String getCompletionCondition() {
        return completionCondition;
    }

    public boolean hasLoopDataInputRef() {
        return loopDataInputRef != null;
    }

    public boolean hasLoopDataOutputRef() {
        return loopDataOutputRef != null;
    }

    public boolean isSequential() {
        return isSequential;
    }

    public void setSequential(boolean isSequential) {
        this.isSequential = isSequential;
    }
}
