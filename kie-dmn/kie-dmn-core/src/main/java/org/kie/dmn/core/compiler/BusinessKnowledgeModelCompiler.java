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
package org.kie.dmn.core.compiler;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.SimpleFnTypeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.InformationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.DecisionCompiler.loadInCtx;

public class BusinessKnowledgeModelCompiler implements DRGElementCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessKnowledgeModelCompiler.class);
    @Override
    public boolean accept(DRGElement de) {
        return de instanceof BusinessKnowledgeModel;
    }
    @Override
    public void compileNode(DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model) {
        BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) de;
        BusinessKnowledgeModelNodeImpl bkmn = new BusinessKnowledgeModelNodeImpl( bkm );
        DMNType type;
        DMNType fnType = null;
        if ( bkm.getVariable() == null ) {
            DMNCompilerHelper.reportMissingVariable( model, de, bkm, Msg.MISSING_VARIABLE_FOR_BKM );
            return;
        }
        DMNCompilerHelper.checkVariableName( model, bkm, bkm.getName() );
        if (bkm.getVariable().getTypeRef() != null) { // variable must be present, otherwise error was already reported above.
            type = compiler.resolveTypeRef(model, bkm, bkm.getVariable(), bkm.getVariable().getTypeRef());
            if (type instanceof SimpleFnTypeImpl) {
                fnType = type;
                type = ((SimpleFnTypeImpl) type).getReturnType();
            }
            // consistency checks
            if (bkm.getEncapsulatedLogic() != null && bkm.getEncapsulatedLogic().getTypeRef() != null) {
                DMNType bkmELType = compiler.resolveTypeRef(model, bkm, bkm.getEncapsulatedLogic(), bkm.getEncapsulatedLogic().getTypeRef());
                if (!areSameDMNType(fnType != null ? fnType : type, bkmELType)) {
                    MsgUtil.reportMessage( LOG,
                            DMNMessage.Severity.WARN,
                            bkm.getEncapsulatedLogic(),
                            model,
                            null,
                            null,
                            Msg.VARIABLE_TYPE_MISMATCH_FOR_BKM_EL,
                            bkm.getEncapsulatedLogic().getTypeRef(),
                            bkm.getVariable().getTypeRef() );
                }
            }
            if (fnType == null && bkm.getEncapsulatedLogic() != null && bkm.getEncapsulatedLogic().getExpression() != null && bkm.getEncapsulatedLogic().getExpression().getTypeRef() != null) {
                // if fnType != null, this is already taken care of DROOLS-6488 in method checkFnConsistency, further below.
                DMNType elExprType = compiler.resolveTypeRef(model, bkm, bkm.getEncapsulatedLogic().getExpression(), bkm.getEncapsulatedLogic().getExpression().getTypeRef());
                if (!areSameDMNType(type, elExprType)) {
                    MsgUtil.reportMessage( LOG,
                            DMNMessage.Severity.WARN,
                            bkm.getEncapsulatedLogic(),
                            model,
                            null,
                            null,
                            Msg.VARIABLE_TYPE_MISMATCH_FOR_BKM_EL_BODY,
                            bkm.getEncapsulatedLogic().getExpression().getTypeRef(),
                            type.getName() );
                }
            }
        } else if (bkm.getVariable().getTypeRef() == null && bkm.getEncapsulatedLogic().getExpression() != null && bkm.getEncapsulatedLogic().getExpression().getTypeRef() != null) {
            type = compiler.resolveTypeRef(model, bkm, bkm.getEncapsulatedLogic().getExpression(), bkm.getEncapsulatedLogic().getExpression().getTypeRef());
        } else {
            // for now the call bellow will return type UNKNOWN
            type = compiler.resolveTypeRef(model, bkm, bkm, null);
        }
        bkmn.setType(fnType);
        bkmn.setResultType( type );
        model.addBusinessKnowledgeModel( bkmn );
    }
    private static boolean areSameDMNType(DMNType type, DMNType bkmELType) {
        return type.getName().equals(bkmELType.getName()) && type.getNamespace().equals(bkmELType.getNamespace());
    }
    @Override
    public boolean accept(DMNNode node) {
        return node instanceof BusinessKnowledgeModelNodeImpl;
    }
    @Override
    public void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) node;
        compiler.linkRequirements( model, bkmi );

        ctx.enterFrame();
        try {
            loadInCtx(bkmi, ctx, model);
            // to allow recursive call from inside a BKM node, a variable for self must be available for the compiler context:
            ctx.setVariable(bkmi.getName(), bkmi.getResultType());
            FunctionDefinition funcDef = bkmi.getBusinessKnowledModel().getEncapsulatedLogic();
            if (bkmi.getType() != null && funcDef != null) {
                checkFnConsistency(model, bkmi, bkmi.getType(), funcDef);
            }
            DMNExpressionEvaluator exprEvaluator = compiler.getEvaluatorCompiler().compileExpression( ctx, model, bkmi, bkmi.getName(), funcDef );
            bkmi.setEvaluator( exprEvaluator );
        } finally {
            ctx.exitFrame();
        }
    }

    private void checkFnConsistency(DMNModelImpl model, BusinessKnowledgeModelNodeImpl bkmi, DMNType type, FunctionDefinition funcDef) {
        SimpleFnTypeImpl fnType = ((SimpleFnTypeImpl) type);
        FunctionItem fi = fnType.getFunctionItem();
        if (fi.getParameters().size() != funcDef.getFormalParameter().size()) {
            MsgUtil.reportMessage(LOG,
                                  DMNMessage.Severity.ERROR,
                                  bkmi.getBusinessKnowledModel(),
                                  model,
                                  null,
                                  null,
                                  Msg.PARAMETER_COUNT_MISMATCH_COMPILING,
                                  bkmi.getName(),
                                  fi.getParameters().size(),
                                  funcDef.getFormalParameter().size());
            return;
        }
        for (int i = 0; i < fi.getParameters().size(); i++) {
            InformationItem fiII = fi.getParameters().get(i);
            InformationItem fdII = funcDef.getFormalParameter().get(i);
            if (!fiII.getName().equals(fdII.getName())) {
                List<String> fiParamNames = fi.getParameters().stream().map(InformationItem::getName).collect(Collectors.toList());
                List<String> funcDefParamNames = funcDef.getFormalParameter().stream().map(InformationItem::getName).collect(Collectors.toList());
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      bkmi.getBusinessKnowledModel(),
                                      model,
                                      null,
                                      null,
                                      Msg.PARAMETER_NAMES_MISMATCH_COMPILING,
                                      bkmi.getName(),
                                      fiParamNames,
                                      funcDefParamNames);
                return;
            }
            QName fiQname = fiII.getTypeRef();
            QName fdQname = fdII.getTypeRef();
            if (fiQname != null && fdQname != null && !fiQname.equals(fdQname)) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      bkmi.getBusinessKnowledModel(),
                                      model,
                                      null,
                                      null,
                                      Msg.PARAMETER_TYPEREF_MISMATCH_COMPILING,
                                      bkmi.getName(),
                                      fiII.getName(),
                                      fiQname,
                                      fdQname);
            }
        }
        QName fiReturnType = fi.getOutputTypeRef();
        QName fdReturnType = funcDef.getExpression().getTypeRef();
        if (fiReturnType != null && fdReturnType != null && !fiReturnType.equals(fdReturnType)) {
            MsgUtil.reportMessage(LOG,
                                  DMNMessage.Severity.ERROR,
                                  bkmi.getBusinessKnowledModel(),
                                  model,
                                  null,
                                  null,
                                  Msg.RETURNTYPE_TYPEREF_MISMATCH_COMPILING,
                                  bkmi.getName(),
                                  fiReturnType,
                                  fdReturnType);
        }
    }
}