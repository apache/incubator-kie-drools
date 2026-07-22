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
package org.jbpm.workflow.core.node;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jbpm.process.instance.impl.ReturnValueEvaluator;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeType;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;

import static org.jbpm.ruleflow.core.Metadata.CUSTOM_AUTO_START;

public class DynamicNode extends CompositeContextNode {

    private static final long serialVersionUID = 510L;

    /**
     * String representation of the activationPredicate. Not used at runtime.
     */
    private String activationCondition;
    /**
     * String representation of the completionPredicate. Not used at runtime.
     */
    private String completionCondition;

    private ReturnValueEvaluator activationPredicate;
    private ReturnValueEvaluator completionPredicate;
    private String language;

    public DynamicNode() {
        super(NodeType.AD_HOC_SUBPROCESS);
        setAutoComplete(false);
    }

    @Override
    public Node internalGetNode(WorkflowElementIdentifier id) {
        try {
            return getNode(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Node> getAutoStartNodes() {
        return Arrays.stream(getNodes())
                .filter(n -> n.getIncomingConnections().isEmpty() && "true".equalsIgnoreCase((String) n.getMetaData().get(CUSTOM_AUTO_START)))
                .collect(Collectors.toList());
    }

    public String getActivationCondition() {
        return activationCondition;
    }

    public void setActivationCondition(String activationCondition) {
        this.activationCondition = activationCondition;
    }

    public String getCompletionCondition() {
        return completionCondition;
    }

    public void setCompletionCondition(String completionCondition) {
        this.completionCondition = completionCondition;
    }

    public DynamicNode setActivationExpression(ReturnValueEvaluator activationPredicate) {
        this.activationPredicate = activationPredicate;
        return this;
    }

    public DynamicNode setCompletionExpression(ReturnValueEvaluator copmletionPredicate) {
        this.completionPredicate = copmletionPredicate;
        return this;
    }

    public boolean canActivate(KogitoProcessContext context) {
        return activationPredicate == null || (Boolean) activationPredicate.evaluate(context);
    }

    public boolean canComplete(KogitoProcessContext context) {
        return isAutoComplete() || (completionPredicate != null && (Boolean) completionPredicate.evaluate(context));
    }

    public boolean hasCompletionCondition() {
        return (completionPredicate != null || (completionCondition != null && !completionCondition.isEmpty()));
    }
}
