/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.DMNDecisionServiceFunctionDefinitionEvaluator;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionServiceCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(DecisionServiceCompiler.class);

    public void compileNode(DecisionService drge, DMNCompilerImpl compiler, DMNModelImpl model) {
        DecisionService ds = (DecisionService) drge;
        DMNType type = null;
        if (ds.getVariable() == null) { // even for the v1.1 backport, variable creation is taken care in DMNCompiler.
            DMNCompilerHelper.reportMissingVariable(model, drge, ds, Msg.MISSING_VARIABLE_FOR_DS);
            return;
        }
        DMNCompilerHelper.checkVariableName(model, ds, ds.getName());
        if (ds.getVariable() != null && ds.getVariable().getTypeRef() != null) {
            type = compiler.resolveTypeRef(model, ds, ds.getVariable(), ds.getVariable().getTypeRef());
        } else {
            // for now the call bellow will return type UNKNOWN
            type = compiler.resolveTypeRef(model, ds, ds, null);
        }
        DecisionServiceNodeImpl bkmn = new DecisionServiceNodeImpl(ds, type);
        model.addDecisionService(bkmn);
    }

    public boolean accept(DMNNode node) {
        return node instanceof DecisionServiceNodeImpl;
    }

    public void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        DecisionServiceNodeImpl ni = (DecisionServiceNodeImpl) node;

        List<FormalParameter> parameters = new ArrayList<>();
        
        for (DMNElementReference er : ni.getDecisionService().getInputData()) {
            String id = DMNCompilerImpl.getId(er);
            InputDataNode input = model.getInputById(id);
            if (input != null) {
                ni.addInputParameter(input.getName(), input);
                parameters.add(new FormalParameter(input.getName(), input.getType()));
            } else {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.REFERENCE_NOT_FOUND_FOR_DS,
                                      id,
                                      node.getName());
            }
        }
        for (DMNElementReference er : ni.getDecisionService().getInputDecision()) {
            String id = DMNCompilerImpl.getId(er);
            DecisionNode input = model.getDecisionById(id);
            if (input != null) {
                ni.addInputParameter(input.getName(), input);
                parameters.add(new FormalParameter(input.getName(), input.getResultType()));
            } else {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.REFERENCE_NOT_FOUND_FOR_DS,
                                      id,
                                      node.getName());
            }
        }
        for (DMNElementReference er : ni.getDecisionService().getEncapsulatedDecision()) {
            String id = DMNCompilerImpl.getId(er);
            DecisionNode input = model.getDecisionById(id);
            if (input != null) {
                // nothing to do.
            } else {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.REFERENCE_NOT_FOUND_FOR_DS,
                                      id,
                                      node.getName());
            }
        }
        for (DMNElementReference er : ni.getDecisionService().getOutputDecision()) {
            String id = DMNCompilerImpl.getId(er);
            DecisionNode input = model.getDecisionById(id);
            if (input != null) {
                // nothing to do.
            } else {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.REFERENCE_NOT_FOUND_FOR_DS,
                                      id,
                                      node.getName());
            }
        }

        boolean coerceSingleton = ((DMNCompilerConfigurationImpl) compiler.getDmnCompilerConfig()).getOption(CoerceDecisionServiceSingletonOutputOption.class).isCoerceSingleton();

        DMNDecisionServiceFunctionDefinitionEvaluator exprEvaluator = new DMNDecisionServiceFunctionDefinitionEvaluator(ni, parameters, coerceSingleton);
        ni.setEvaluator(exprEvaluator);
    }
}