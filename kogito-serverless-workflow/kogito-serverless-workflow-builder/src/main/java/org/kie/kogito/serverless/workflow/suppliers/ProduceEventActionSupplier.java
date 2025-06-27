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
package org.kie.kogito.serverless.workflow.suppliers;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import org.jbpm.compiler.canonical.AbstractNodeVisitor;
import org.jbpm.compiler.canonical.ExpressionSupplier;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.compiler.canonical.descriptors.ExpressionUtils;
import org.jbpm.process.core.event.StaticMessageProducer;
import org.kie.kogito.event.impl.MessageProducerWithContext;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.actions.SWFProduceEventAction;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import io.serverlessworkflow.api.Workflow;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.jbpm.ruleflow.core.Metadata.MAPPING_VARIABLE_INPUT;

public class ProduceEventActionSupplier extends SWFProduceEventAction implements ExpressionSupplier {

    private static final long serialVersionUID = 1L;

    public ProduceEventActionSupplier(Workflow workflow, String trigger, String varName, JsonNode data) {
        this(workflow, trigger, varName, data, Collections.emptyMap());
    }

    public ProduceEventActionSupplier(Workflow workflow, String trigger, String varName, JsonNode data, Map<String, String> contextAttributes) {
        super(trigger, varName, new MessageProducerSupplier(trigger), workflow.getExpressionLang(), data, JsonObjectUtils.fromValue(contextAttributes));
    }

    @Override
    public Expression get(KogitoNode node, ProcessMetaData metadata) {
        return AbstractNodeVisitor.buildProducerAction(parseClassOrInterfaceType(SWFProduceEventAction.class.getCanonicalName()), TriggerMetaData.of(node, (String) node.getMetaData()
                .get(MAPPING_VARIABLE_INPUT)), metadata)
                .addArgument(new StringLiteralExpr(exprLang))
                .addArgument(ExpressionUtils.getLiteralExpr(data))
                .addArgument(ExpressionUtils.getLiteralExpr(contextAttrs));
    }

    private static class MessageProducerSupplier implements Supplier<MessageProducerWithContext<JsonNode>> {

        private MessageProducerWithContext<JsonNode> producer;
        private final String trigger;

        public MessageProducerSupplier(String trigger) {
            this.trigger = trigger;
        }

        @Override
        public MessageProducerWithContext<JsonNode> get() {
            if (producer == null) {
                producer = new StaticMessageProducer<>(trigger);
            }
            return producer;
        }
    }
}
