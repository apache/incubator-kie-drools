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
package org.kie.kogito.serverless.workflow.parser.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.RuleSetNodeFactory;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.dmn.SWFDecisionEngine;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandler;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.parser.handlers.MappingSetter;
import org.kie.kogito.serverless.workflow.parser.handlers.MappingUtils;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

public class DMNTypeHandler implements FunctionTypeHandler {

    private static final String DMN_TYPE = "dmn";
    public static final String NAMESPACE = "namespace";
    public static final String MODEL = "model";
    public static final String FILE = "file";

    private static final String REQUIRED_MESSAGE = "%s is required on metadata for DMN";

    @Override
    public String type() {
        return DMN_TYPE;
    }

    @Override
    public boolean isCustom() {
        return true;
    }

    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow, ParserContext context,
            RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef,
            FunctionRef functionRef, VariableInfo varInfo) {
        Map<String, String> metadata = Objects.requireNonNull(functionDef.getMetadata(), "Metadata is required for DMN");
        String namespace = Objects.requireNonNull(metadata.get(NAMESPACE), String.format(REQUIRED_MESSAGE, NAMESPACE));
        String model = Objects.requireNonNull(metadata.get(MODEL), String.format(REQUIRED_MESSAGE, MODEL));
        String file = Objects.requireNonNull(metadata.get(FILE), String.format(REQUIRED_MESSAGE, FILE));
        RuleSetNodeFactory<?> nodeFactory = MappingUtils.addMapping(embeddedSubProcess.ruleSetNode(context.newId()).decision(namespace, model, model, () -> loadDMNFromFile(namespace, model, file)),
                varInfo.getInputVar(), varInfo.getOutputVar());
        JsonNode functionArgs = functionRef.getArguments();
        if (functionArgs != null) {
            nodeFactory.metaData(SWFDecisionEngine.EXPR_LANG, workflow.getExpressionLang());
            MappingUtils.processArgs(workflow, functionArgs, new MappingSetter() {
                @Override
                public void accept(String key, Object value) {
                    nodeFactory.parameter(key, value);
                }

                @Override
                public void accept(Object value) {
                    nodeFactory.parameter(SWFConstants.CONTENT_DATA, value);
                }
            });
        }
        return nodeFactory;
    }

    private DecisionModel loadDMNFromFile(String namespace, String model, String file) {
        try (Reader reader = new InputStreamReader(URIContentLoaderFactory.builder(file).withClassloader(this.getClass().getClassLoader()).build().getInputStream())) {
            return new DmnDecisionModel(DMNKogito.createGenericDMNRuntime(Collections.emptySet(), false, reader), namespace, model);
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
    }
}
