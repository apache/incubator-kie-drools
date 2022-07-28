/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workflow.core;

import java.util.Optional;
import java.util.function.BiFunction;

import org.jbpm.process.core.Process;
import org.jbpm.process.instance.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

/**
 * Represents a RuleFlow process.
 * 
 */
public interface WorkflowProcess extends KogitoWorkflowProcess, Process, NodeContainer {

    int PROCESS_TYPE = 1;
    int CASE_TYPE = 2;

    /**
     * Returns whether this process will automatically complete if it
     * contains no active node instances anymore
     * 
     * @return the names of the globals of this RuleFlow process
     */
    boolean isAutoComplete();

    boolean isDynamic();

    Integer getProcessType();

    void setExpressionEvaluator(BiFunction<String, ProcessInstance, String> expressionEvaluator);

    String evaluateExpression(String metaData, ProcessInstance processInstance);

    Optional<WorkflowInputModelValidator> getValidator();

    void setValidator(WorkflowInputModelValidator validator);

}
