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
package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNDecisionServiceFunctionDefinitionEvaluator;
import org.kie.dmn.core.ast.DMNDecisionServiceFunctionDefinitionEvaluator.DSFormalParameter;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.core.ast.ItemDefNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.SimpleFnTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.core.util.NamespaceUtil;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.typesafe.DMNTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;

public class DecisionServiceCompiler implements DRGElementCompiler {

    private static final Logger LOG = LoggerFactory.getLogger(DecisionServiceCompiler.class);

    @Override
    public boolean accept(DRGElement de) {
        return de instanceof DecisionService;
    }

    /** backport of DMN v1.1
     * 
     */
    public void compileNode(DecisionService ds, DMNCompilerImpl compiler, DMNModelImpl model) {
        DMNType type;
        DMNType fnType = null;
        if (ds.getVariable() == null) { // even for the v1.1 backport, variable creation is taken care in DMNCompiler.
            DMNCompilerHelper.reportMissingVariable(model, ds, ds, Msg.MISSING_VARIABLE_FOR_DS);
            return;
        }
        DMNCompilerHelper.checkVariableName(model, ds, ds.getName());
        if (ds.getVariable() != null && ds.getVariable().getTypeRef() != null) {
            type = compiler.resolveTypeRef(model, ds, ds.getVariable(), ds.getVariable().getTypeRef());
            if (type instanceof SimpleFnTypeImpl) {
                fnType = type;
                type = ((SimpleFnTypeImpl) type).getReturnType();
            }
        } else {
            // for now the call bellow will return type UNKNOWN
            type = compiler.resolveTypeRef(model, ds, ds, null);
        }
        DecisionServiceNodeImpl bkmn = new DecisionServiceNodeImpl(ds, fnType, type);
        model.addDecisionService(bkmn);
    }

    @Override
    public void compileNode(DRGElement drge, DMNCompilerImpl compiler, DMNModelImpl model) {
        DecisionService ds = (DecisionService) drge;
        compileNode(ds, compiler, model);
    }

    @Override
    public boolean accept(DMNNode node) {
        return node instanceof DecisionServiceNodeImpl;
    }

    /**
     * DMN v1.2 specification, chapter "10.4 Execution Semantics of Decision Services"
     * The qualified name of an element named E that is defined in the same decision model as S is simply E.
     * Otherwise, the qualified name is I.E, where I is the name of the import element that refers to the model where E is defined.
     */
     static String inputQualifiedNamePrefix(DMNNode input, DMNModelImpl model) {
        if (input.getModelNamespace().equals(model.getNamespace()) || isInUnnamedImport(input, model)) {
            return null;
        } else {
            try {
                return getInputNamePrefix(input, model);
            } catch (IllegalStateException e) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ((DMNBaseNode)input).getSource(),
                                      model,
                                      null,
                                      null,
                                      Msg.IMPORT_NOT_FOUND_FOR_NODE_MISSING_ALIAS,
                                      new QName(input.getModelNamespace(), input.getModelName()),
                                      ((DMNBaseNode)input).getSource());
                return null;
            }
        }
     }

    private static String getInputNamePrefix(DMNNode input, DMNModelImpl model) {
        Optional<String> importAlias = model.getImportAliasFor(input.getModelNamespace(), input.getModelName());
        if (importAlias.isEmpty()) {
            throw new IllegalStateException("Missing import alias for model " + input.getModelName() +
                    "with namespace " + input.getModelNamespace());
        }
        return importAlias.get();
    }

    @Override
    public void compileEvaluator(DMNNode node, DMNCompilerImpl compiler, DMNCompilerContext ctx, DMNModelImpl model) {
        DecisionServiceNodeImpl ni = (DecisionServiceNodeImpl) node;
        List<DSFormalParameter> parameters = new ArrayList<>();
        processInputData(ni, model, parameters);
        processInputDecisions(ni, model, parameters);
        validateEncapsulatedDecision(ni, model);
        List<DecisionNode> outputDecisions = getOutputDecisions(ni, model);

        boolean coerceSingleton = ((DMNCompilerConfigurationImpl) compiler.getDmnCompilerConfig()).getOption(CoerceDecisionServiceSingletonOutputOption.class).isCoerceSingleton();
        DMNDecisionServiceFunctionDefinitionEvaluator exprEvaluator = new DMNDecisionServiceFunctionDefinitionEvaluator(ni, parameters, coerceSingleton);
        ni.setEvaluator(exprEvaluator);

        if (ni.getType() != null) {
            checkFnConsistency(model, ni, ni.getType(), outputDecisions);
        }
    }

    private void processInputData(DecisionServiceNodeImpl ni, DMNModelImpl model, List<DSFormalParameter> parameters) {
        for (DMNElementReference er : ni.getDecisionService().getInputData()) {
            String id = DMNCompilerImpl.getReferenceId(er);
            InputDataNode input = model.getInputById(id);
            if (input != null) {
                String inputNamePrefix = inputQualifiedNamePrefix(input, model);
                ni.addInputParameter(inputNamePrefix != null ? inputNamePrefix + "." + input.getName() : input.getName(), input);
                parameters.add(new DSFormalParameter(inputNamePrefix, input.getName(), input.getType()));
            } else {
                reportReferenceError(ni, model, id);
            }
        }
    }

    private void processInputDecisions(DecisionServiceNodeImpl ni, DMNModelImpl model, List<DSFormalParameter> parameters) {
        for (DMNElementReference er : ni.getDecisionService().getInputDecision()) {
            String id = DMNCompilerImpl.getReferenceId(er);
            DecisionNode input = model.getDecisionById(id);
            if (input != null) {
                String inputNamePrefix = inputQualifiedNamePrefix(input, model);
                ni.addInputParameter(inputNamePrefix != null ? inputNamePrefix + "." + input.getName() : input.getName(), input);
                parameters.add(new DSFormalParameter(inputNamePrefix, input.getName(), input.getResultType()));
            } else {
                reportReferenceError(ni, model, id);
            }
        }
    }

    private void validateEncapsulatedDecision(DecisionServiceNodeImpl ni, DMNModelImpl model) {
        for (DMNElementReference er : ni.getDecisionService().getEncapsulatedDecision()) {
            String id = DMNCompilerImpl.getReferenceId(er);
            if (model.getDecisionById(id) == null) {
                reportReferenceError(ni, model, id);
            }
        }
    }

    private List<DecisionNode> getOutputDecisions(DecisionServiceNodeImpl ni, DMNModelImpl model) {
        List<DecisionNode> outputDecisions = new ArrayList<>();
        for (DMNElementReference er : ni.getDecisionService().getOutputDecision()) {
            String id = DMNCompilerImpl.getReferenceId(er);
            DecisionNode outDecision = model.getDecisionById(id);
            if (outDecision != null) {
                outputDecisions.add(outDecision);
            } else {
                reportReferenceError(ni, model, id);
            }
        }
        return outputDecisions;
    }

    private void reportReferenceError(DecisionServiceNodeImpl ni, DMNModelImpl model, String id) {
        MsgUtil.reportMessage(LOG,
                DMNMessage.Severity.ERROR,
                ni.getDecisionService(),
                model,
                null,
                null,
                Msg.REFERENCE_NOT_FOUND_FOR_DS,
                id,
                ni.getName());
    }

    private void checkFnConsistency(DMNModelImpl model, DecisionServiceNodeImpl ni, DMNType type, List<DecisionNode> outputDecisions) {
        SimpleFnTypeImpl fnType = ((SimpleFnTypeImpl) type);
        FunctionItem fi = fnType.getFunctionItem();
        if (fi.getParameters().size() != ni.getInputParameters().size()) {
            MsgUtil.reportMessage(LOG,
                                  DMNMessage.Severity.ERROR,
                                  ni.getDecisionService(),
                                  model,
                                  null,
                                  null,
                                  Msg.PARAMETER_COUNT_MISMATCH_COMPILING,
                                  ni.getName(),
                                  fi.getParameters().size(),
                                  ni.getInputParameters().size());
            return;
        }
        for (int i = 0; i < fi.getParameters().size(); i++) {
            InformationItem fiII = fi.getParameters().get(i);
            String fpName = ni.getInputParameters().keySet().stream().skip(i).findFirst().orElse(null);
            if (!fiII.getName().equals(fpName)) {
                List<String> fiParamNames = fi.getParameters().stream().map(InformationItem::getName).collect(Collectors.toList());
                List<String> funcDefParamNames = ni.getInputParameters().keySet().stream().collect(Collectors.toList());
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.PARAMETER_NAMES_MISMATCH_COMPILING,
                                      ni.getName(),
                                      fiParamNames,
                                      funcDefParamNames);
                return;
            }
            QName fiQname = fiII.getTypeRef();
            QName fdQname = null;
            DMNNode fpDMNNode = ni.getInputParameters().get(fpName);
            if (fpDMNNode instanceof InputDataNodeImpl) {
                fdQname = ((InputDataNodeImpl) fpDMNNode).getInputData().getVariable().getTypeRef();
            } else if (fpDMNNode instanceof DecisionNodeImpl) {
                fdQname = ((DecisionNodeImpl) fpDMNNode).getDecision().getVariable().getTypeRef();
            }
            if (fiQname != null && fdQname != null && !fiQname.equals(fdQname)) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.PARAMETER_TYPEREF_MISMATCH_COMPILING,
                                      ni.getName(),
                                      fiII.getName(),
                                      fiQname,
                                      fdQname);
            }
        }
        QName fiReturnType = fi.getOutputTypeRef();
        if (ni.getDecisionService().getOutputDecision().size() == 1) {
            QName fdReturnType = outputDecisions.get(0).getDecision().getVariable().getTypeRef();
            if (fiReturnType != null && fdReturnType != null && !fiReturnType.equals(fdReturnType) && !isReturnTypeCollectionCompatible(fiReturnType, fdReturnType, model)) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.RETURNTYPE_TYPEREF_MISMATCH_COMPILING,
                                      ni.getName(),
                                      fiReturnType,
                                      fdReturnType);
            }
        } else if (ni.getDecisionService().getOutputDecision().size() > 1) {
            final Function<QName, QName> lookupFn = (in) -> NamespaceUtil.getNamespaceAndName(ni.getDecisionService(), model.getImportAliasesForNS(), in, model.getNamespace());
            LinkedHashMap<String, QName> fdComposite = new LinkedHashMap<>();
            for (DecisionNode dn : outputDecisions) {
                fdComposite.put(dn.getName(), lookupFn.apply(dn.getDecision().getVariable().getTypeRef()));
            }
            final QName lookup = lookupFn.apply(fiReturnType);
            Optional<ItemDefNodeImpl> composite = model.getItemDefinitions().stream().filter(id -> id.getModelNamespace().equals(lookup.getNamespaceURI()) && id.getName().equals(lookup.getLocalPart())).map(ItemDefNodeImpl.class::cast).findFirst();
            if (composite.isEmpty()) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.RETURNTYPE_TYPEREF_MISMATCH_COMPILING,
                                      ni.getName(),
                                      lookup,
                                      fdComposite);
                return;
            }
            LinkedHashMap<String, QName> fiComposite = new LinkedHashMap<>();
            for (ItemDefinition ic : composite.get().getItemDef().getItemComponent()) {
                fiComposite.put(ic.getName(), lookupFn.apply(ic.getTypeRef()));
            }
            if (!fiComposite.equals(fdComposite)) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      ni.getDecisionService(),
                                      model,
                                      null,
                                      null,
                                      Msg.RETURNTYPE_TYPEREF_MISMATCH_COMPILING,
                                      ni.getName(),
                                      fiComposite,
                                      fdComposite);
            }
        }
    }

    private boolean isReturnTypeCollectionCompatible(QName fiReturnType, QName fdReturnType, DMNModelImpl model) {
        DMNType fiType = resolveDMNType(fiReturnType, model);
        DMNType fdType = resolveDMNType(fdReturnType, model);

        if (!fiType.isCollection() && fdType.isCollection()) {
            DMNType base = fdType.getBaseType();
            return base == null || DMNTypeUtils.getFEELBuiltInType(base)
                    == DMNTypeUtils.getFEELBuiltInType(fiType);
        }
        if (fiType.isCollection() && !fdType.isCollection()) {
            DMNType base = fiType.getBaseType();
            return base != null && DMNTypeUtils.getFEELBuiltInType(base)
                    == DMNTypeUtils.getFEELBuiltInType(fdType);
        }
        return false;
    }

    private DMNType resolveDMNType(QName qName, DMNModelImpl model) {
        DMNType type = model.getTypeRegistry().resolveType(model.getNamespace(), qName.getLocalPart());
        if (type == null) {
            BuiltInType bi = resolveBuiltInType(qName.getLocalPart());
            if (bi != null) {
                boolean isCollection = (bi == BuiltInType.LIST);
                type = new SimpleTypeImpl(
                        model.getNamespace(),
                        bi.getName(),
                        null,
                        isCollection,
                        null,
                        null,
                        null,
                        bi
                );
            }
        }
        return type;
    }


    private BuiltInType resolveBuiltInType(String name) {
        if (name == null) return null;
        for (BuiltInType t : BuiltInType.values()) {
            for (String n : t.getNames()) {
                if (n.equalsIgnoreCase(name)) {
                    return t;
                }
            }
        }
        return null;
    }

}