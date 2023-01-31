/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.feel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Split;
import org.kie.api.definition.process.Node;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;

import static java.lang.String.format;

/**
 * Feel validator.
 */
public class FeelProcessValidator extends RuleFlowProcessValidator {

    private static FeelProcessValidator INSTANCE;

    private FeelProcessValidator() {
        super();
    }

    public static FeelProcessValidator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FeelProcessValidator();
        }
        return INSTANCE;
    }

    @Override
    protected void validateNodes(Node[] nodes, List<ProcessValidationError> errors, RuleFlowProcess process) {
        super.validateNodes(nodes, errors, process);
        Arrays.stream(nodes).filter(n -> n instanceof Split).forEach(node -> {
            final Split split = (Split) node;
            if (split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR) {
                for (Map.Entry<ConnectionRef, Constraint> entry : split.getConstraints().entrySet()) {
                    if (entry.getValue() != null && "FEEL".equals(entry.getValue().getDialect())) {
                        try {
                            verifyFEELbyCompilingExpression(process.getVariableScope(), entry.getValue().getConstraint());
                        } catch (FeelCompilationException ex) {
                            addErrorMessage(process,
                                    node,
                                    errors,
                                    format("Invalid FEEL expression: '%s'.", entry.getValue().getConstraint()));
                        }
                    }
                }
            }
        });
    }

    /**
     * Instead of throwing a generic JavaParser compilation error (atm happens for invalid expression of dialect=JAVA)
     * use the FEEL compiler capabilities to verify if mere compilation of the FEEL expression may contain any error.
     */
    private void verifyFEELbyCompilingExpression(VariableScope variableScope, String feelExpression) {
        FEEL feel = FEEL.newInstance(Collections.singletonList(new KieExtendedFEELProfile()));
        FeelErrorEvaluatorListener feelErrorListener = new FeelErrorEvaluatorListener();
        feel.addListener(feelErrorListener);
        CompilerContext cc = feel.newCompilerContext();
        for (Variable v : variableScope.getVariables()) {
            cc.addInputVariable(v.getName(), null);
        }
        feel.compile(feelExpression, cc);
        if (!feelErrorListener.getErrorEvents().isEmpty()) {
            String exceptionMessage = feelErrorListener.getErrorEvents().stream().map(FeelReturnValueEvaluator::eventToMessage).collect(Collectors.joining(", "));
            throw new FeelCompilationException(exceptionMessage);
        }
    }

}
