/**
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
package org.kie.dmn.core.ast;

import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FEEL does not define the return type for function definition in its grammar.
 * While on FEEL layer the type could be inferred, at DMN layer this is governed in BKM case by the <variable typeRef value.
 * This is typically used to typecheck the result of a BKM function evaluation result.
 */
public class DMNFunctionWithReturnType extends BaseFEELFunction {

    private static final Logger LOG = LoggerFactory.getLogger(DMNFunctionWithReturnType.class);

    private final FEELFunction wrapped;
    private final DMNType returnType;
    private final DMNMessageManager msgMgr;
    private final BusinessKnowledgeModelNode node;

    public DMNFunctionWithReturnType(FEELFunction wrapped, DMNType returnType, DMNMessageManager msgMgr, BusinessKnowledgeModelNode node) {
        super(wrapped.getName());
        this.wrapped = wrapped;
        this.returnType = returnType;
        this.msgMgr = msgMgr;
        this.node = node;
    }

    @Override
    public Object invokeReflectively(EvaluationContext ctx, Object[] params) {
        Object result = wrapped.invokeReflectively(ctx, params);
        result = DMNRuntimeImpl.coerceUsingType(result,
                                                returnType,
                                                true, // this FN is created when typeCheck==true, hence here always true.
                                                (r, t) -> MsgUtil.reportMessage(LOG,
                                                                                DMNMessage.Severity.WARN,
                                                                                node.getBusinessKnowledModel(),
                                                                                msgMgr,
                                                                                null,
                                                                                null,
                                                                                Msg.ERROR_EVAL_NODE_RESULT_WRONG_TYPE,
                                                                                node.getName() != null ? node.getName() : node.getId(),
                                                                                t,
                                                                                MsgUtil.clipString(r.toString(), 50)));
        return result;
    }

    @Override
    public Symbol getSymbol() {
        return wrapped.getSymbol();
    }

    @Override
    public List<List<Param>> getParameters() {
        return wrapped.getParameters();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    public String toString() {
        return wrapped.toString();
    }

}
