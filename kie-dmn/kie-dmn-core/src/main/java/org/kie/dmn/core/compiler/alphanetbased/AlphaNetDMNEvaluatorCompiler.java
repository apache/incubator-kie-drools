/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.ancompiler.CompiledNetwork;
import org.drools.ancompiler.CompiledNetworkSource;
import org.drools.ancompiler.ObjectTypeNodeCompiler;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.compiler.alphanetbased.evaluator.OutputClausesWithType;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlphaNetDMNEvaluatorCompiler extends DMNEvaluatorCompiler {

    static final Logger logger = LoggerFactory.getLogger(AlphaNetDMNEvaluatorCompiler.class);

    public AlphaNetDMNEvaluatorCompiler(DMNCompilerImpl compiler) {
        super(compiler);
    }

    @Override
    protected DMNExpressionEvaluator compileDecisionTable(DMNCompilerContext dmnCompilerContext,
                                                          DMNModelImpl dmnModelImpl,
                                                          DMNBaseNode dmnBaseNode,
                                                          String decisionTableName,
                                                          DecisionTable decisionTable) {

        DMNFEELHelper feelHelper = dmnCompilerContext.getFeelHelper();
        CompilerContext compilerContext = dmnCompilerContext.toCompilerContext();

        // Parse every cell in the Decision Table
        TableCell.TableCellFactory tableCellFactory = new TableCell.TableCellFactory(feelHelper, compilerContext);
        TableCellParser tableCellParser = new TableCellParser(tableCellFactory);
        DTQNameToTypeResolver resolver = new DTQNameToTypeResolver(compiler, dmnModelImpl, dmnBaseNode.getSource(), decisionTable);
        TableCells tableCells = tableCellParser.parseCells(decisionTable, resolver, decisionTableName);

        GeneratedSources allGeneratedSources = new GeneratedSources();

        // Compile FEEL unary tests to Java source code with row,column index.
        // i.e. second row third column will have the UnaryTestR2C3.java name
        Map<String, String> feelTestClasses = tableCells.createFEELSourceClasses();
        allGeneratedSources.putAllGeneratedFEELTestClasses(feelTestClasses);

        // Generate classes for DMNAlphaNetwork
        DMNAlphaNetworkCompiler dmnAlphaNetworkCompiler = new DMNAlphaNetworkCompiler();
        GeneratedSources generatedSources = dmnAlphaNetworkCompiler.generateSourceCode(decisionTable, tableCells, decisionTableName, allGeneratedSources);

        // Instantiate Alpha Network
        Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(generatedSources.getAllGeneratedSources(), this.getClass().getClassLoader());
        DMNCompiledAlphaNetwork dmnCompiledAlphaNetwork = generatedSources.newInstanceOfAlphaNetwork(compiledClasses);

        // We need the RETE to create the ANC
        dmnCompiledAlphaNetwork.initRete();

        // Generate the ANC
        ObjectTypeNodeCompiler objectTypeNodeCompiler = new ObjectTypeNodeCompiler(dmnCompiledAlphaNetwork.getObjectTypeNode());
        CompiledNetworkSource compiledNetworkSource = objectTypeNodeCompiler.generateSource();
        generatedSources.dumpGeneratedAlphaNetwork(compiledNetworkSource);

        // Second compilation, this time for the generated ANC sources
        Map<String, Class<?>> compiledANC = KieMemoryCompiler.compile(Collections.singletonMap(
                compiledNetworkSource.getName(), compiledNetworkSource.getSource()), this.getRootClassLoader());

        Class<?> aClass = compiledANC.get(compiledNetworkSource.getName());
        CompiledNetwork compiledAlphaNetwork = compiledNetworkSource.createInstanceAndSet(aClass);
        dmnCompiledAlphaNetwork.setCompiledAlphaNetwork(compiledAlphaNetwork);

        // FeelDecisionTable is used at runtime to evaluate Hit Policy / Output values
        // TODO DT-ANC probably need to have all the types in here
        Map<String, Type> variableTypes = new HashMap<>();
        OutputClausesWithType outputClausesWithType = new OutputClausesWithType(dmnModelImpl, decisionTable);
        List<OutputClausesWithType.OutputClauseWithType> outputs = outputClausesWithType.inferTypeForOutputClauses(decisionTable.getOutput());

        FeelDecisionTable feelDecisionTable = new FeelDecisionTable(decisionTableName, outputs, feelHelper, variableTypes, dmnModelImpl.getTypeRegistry().unknown());

        return new AlphaNetDMNExpressionEvaluator(dmnCompiledAlphaNetwork, feelHelper, decisionTableName, feelDecisionTable, dmnBaseNode);
    }
}
