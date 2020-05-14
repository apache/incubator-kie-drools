/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeEventManagerUtils;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNDecisionServiceEvaluator implements DMNExpressionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServiceEvaluator.class);
    private DecisionServiceNode dsNode;
    private boolean transferResult;
    private boolean coerceSingletonResult;


    public DMNDecisionServiceEvaluator(DecisionServiceNode dsNode, boolean transferResult, boolean coerceSingletonResult) {
        this.dsNode = dsNode;
        this.transferResult = transferResult;
        this.coerceSingletonResult = coerceSingletonResult;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult r) {
        DMNResultImpl result = (DMNResultImpl) r;
        DMNRuntimeEventManagerUtils.fireBeforeEvaluateDecisionService(eventManager, dsNode, result);
        DMNRuntime dmnRuntime = eventManager.getRuntime();
        DMNModel dmnModel = dmnRuntime.getModel(dsNode.getModelNamespace(), dsNode.getModelName());
        List<String> decisionIDs = dsNode.getDecisionService().getOutputDecision().stream().map(er -> DMNCompilerImpl.getId(er)).collect(Collectors.toList());
        DMNResult evaluateById = dmnRuntime.evaluateById(dmnModel, result.getContext().clone(), decisionIDs.toArray(new String[]{}));
        Map<String, Object> ctx = new HashMap<String, Object>();
        List<DMNDecisionResult> decisionResults = new ArrayList<>();
        for (String id : decisionIDs) {
            DMNDecisionResult decisionResultById = evaluateById.getDecisionResultById(id);
            String decisionName = dmnModel.getDecisionById(id).getName();
            ctx.put(decisionName, decisionResultById.getResult());
            decisionResults.add(decisionResultById);
        }
        boolean errors = false;
        for (DMNMessage m : evaluateById.getMessages()) {
            result.addMessage(m);
            if (m.getSeverity() == Severity.ERROR) {
                errors = true;
            }
        }
        boolean typeCheck = ((DMNRuntimeImpl) eventManager.getRuntime()).performRuntimeTypeCheck(result.getModel());
        if (typeCheck) {
            Object c = DMNRuntimeImpl.coerceUsingType(ctx,
                                                      dsNode.getResultType(),
                                                      typeCheck,
                                                      (rx, tx) -> MsgUtil.reportMessage(LOG,
                                                                                        DMNMessage.Severity.WARN,
                                                                                        dsNode.getDecisionService(),
                                                                                        result,
                                                                                        null,
                                                                                        null,
                                                                                        Msg.ERROR_EVAL_NODE_RESULT_WRONG_TYPE,
                                                                                        dsNode.getDecisionService().getName() != null ? dsNode.getDecisionService().getName() : dsNode.getDecisionService().getId(),
                                                                                        tx,
                                                                                        MsgUtil.clipString(rx.toString(), 50)));
            if (c == null) {
                ctx.clear();
                decisionResults.clear();
            }
        }
        for (DMNDecisionResult dr : decisionResults) {
            result.getContext().set(dr.getDecisionName(), dr.getResult());
            if (transferResult) {
                result.addDecisionResult(dr);
            }
        }
        DMNRuntimeEventManagerUtils.fireAfterEvaluateDecisionService(eventManager, dsNode, result);
        Object evaluatorResultValue = ctx;
        if (decisionIDs.size() == 1 && coerceSingletonResult) {
            evaluatorResultValue = ctx.values().iterator().next();
        }
        ResultType resultType = ResultType.SUCCESS;
        if (errors) {
            resultType = ResultType.FAILURE;
            MsgUtil.reportMessage(LOG,
                                  DMNMessage.Severity.ERROR,
                                  ((DecisionServiceNodeImpl) dsNode).getSource(),
                                  result,
                                  null,
                                  null,
                                  Msg.ERRORS_EVAL_DS_NODE,
                                  dsNode.getName());
        }
        return new EvaluatorResultImpl(evaluatorResultValue, resultType);
    }

}
