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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.drools.ancompiler.ANCConfiguration;
import org.drools.ancompiler.CompiledNetwork;
import org.drools.ancompiler.CompiledNetworkSources;
import org.drools.ancompiler.ObjectTypeNodeCompiler;
import org.drools.core.reteoo.ObjectTypeNode;
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

// TODO DT-ANC rename as it's too similar do DMNAlphaNetworkCompiler
public class DMNAlphaNetworkEvaluatorCompiler extends DMNEvaluatorCompiler {

    static final Logger logger = LoggerFactory.getLogger(DMNAlphaNetworkEvaluatorCompiler.class);

    public DMNAlphaNetworkEvaluatorCompiler(DMNCompilerImpl compiler) {
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


        DMNAlphaNetworkCompiler dmnAlphaNetworkCompiler = new DMNAlphaNetworkCompiler();
        GeneratedSources generatedSources = dmnAlphaNetworkCompiler.generateSourceCode(decisionTable, tableCells, decisionTableName, allGeneratedSources);

        ReteBuilderContext reteBuilderContext = new ReteBuilderContext();
        ObjectTypeNode firstObjectTypeNodeOfRete = tableCells.createRete(reteBuilderContext);

        // Compile FEEL unary tests to Java source code with row,column index.
        Map<String, String> feelTestClasses = tableCells.createFEELSourceClasses();
        allGeneratedSources.putAllGeneratedFEELTestClasses(feelTestClasses);

        // Generate the ANC
        ObjectTypeNodeCompiler objectTypeNodeCompiler = createAlphaNetworkCompiler(firstObjectTypeNodeOfRete);
        CompiledNetworkSources compiledNetworkSource = objectTypeNodeCompiler.generateSource();
        generatedSources.addNewSourceClasses(compiledNetworkSource.getAllGeneratedSources());

        // Look at target/generated-sources
        generatedSources.dumpGeneratedClasses();

        // Compile everything
        Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(generatedSources.getAllGeneratedSources(), this.getClass().getClassLoader());

        Class<?> compiledNetworkClass = compiledClasses.get(compiledNetworkSource.getName());
        CompiledNetwork compiledAlphaNetwork = compiledNetworkSource.createInstanceAndSet(compiledNetworkClass);
        Results results = new Results();
        AlphaNetworkEvaluationContext evaluationContext = new AlphaNetworkEvaluationContext(results);
        DMNAlphaNetworkEvaluator dmnCompiledAlphaNetworkEvaluator = generatedSources
                .newInstanceOfAlphaNetwork(compiledClasses, compiledAlphaNetwork, evaluationContext);

        // FeelDecisionTable is used at runtime to evaluate Hit Policy / Output values
        // TODO DT-ANC probably need to have all the types in here
        Map<String, Type> variableTypes = new HashMap<>();
        OutputClausesWithType outputClausesWithType = new OutputClausesWithType(dmnModelImpl, decisionTable);
        List<OutputClausesWithType.OutputClauseWithType> outputs = outputClausesWithType.inferTypeForOutputClauses(decisionTable.getOutput());

        FeelDecisionTable feelDecisionTable = new FeelDecisionTable(decisionTableName, outputs, feelHelper, variableTypes, dmnModelImpl.getTypeRegistry().unknown());

        return new DMNAlphaNetworkEvaluatorImpl(dmnCompiledAlphaNetworkEvaluator, feelHelper, decisionTableName, decisionTable.getId(), feelDecisionTable, dmnBaseNode, results);
    }

    private ObjectTypeNodeCompiler createAlphaNetworkCompiler(ObjectTypeNode firstObjectTypeNodeOfRete) {
        ANCConfiguration ancConfiguration = new ANCConfiguration();
        ancConfiguration.setDisableContextEntry(true);
        ancConfiguration.setPrettyPrint(true);
        ancConfiguration.setEnableModifyObject(false);
        ObjectTypeNodeCompiler objectTypeNodeCompiler = new ObjectTypeNodeCompiler(ancConfiguration, firstObjectTypeNodeOfRete, true);
        VariableDeclarator variableDeclarator = new VariableDeclarator(StaticJavaParser.parseType(AlphaNetworkEvaluationContext.class.getCanonicalName()), "ctx");
        objectTypeNodeCompiler.addAdditionalFields(new FieldDeclaration(NodeList.nodeList(), NodeList.nodeList(variableDeclarator)));
        return objectTypeNodeCompiler;
    }
}
