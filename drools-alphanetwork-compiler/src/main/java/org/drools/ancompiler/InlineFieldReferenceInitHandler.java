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
package org.drools.ancompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.Sink;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.ancompiler.AbstractCompilerHandler.getVariableName;
import static org.drools.ancompiler.AbstractCompilerHandler.getVariableType;
import static org.drools.util.StringUtils.ucFirst;

/**
 * This handler is used to inline the creation of a constraint and a ResultCollectorSink
 * So that it doesn't depend on the RETE at runtime
 */
public class InlineFieldReferenceInitHandler {

    private static final String METHOD_NAME = "initConstraintsResults";

    private static final String statementCall = "        " +
            " {" +
            "   initNodeN();\n" +
            "}";

    private final List<NetworkNode> nodes;
    private List<FieldDeclaration> additionalFields;

    public InlineFieldReferenceInitHandler(List<NetworkNode> nodes,
                                           List<FieldDeclaration> additionalFields) {
        this.nodes = nodes;
        this.additionalFields = additionalFields;
    }

    private final Map<Integer, CompilationUnit> partitionedNodeInitialisationClasses = new HashMap<>();

    public Collection<CompilationUnit> getPartitionedNodeInitialisationClasses() {
        return partitionedNodeInitialisationClasses.values();
    }

    public void emitCode(StringBuilder builder) {

        List<MethodDeclaration> allMethods = new ArrayList<>();

        MethodDeclaration methodDeclaration = new MethodDeclaration(
                nodeList(Modifier.publicModifier()),
                new VoidType(),
                METHOD_NAME
        );

        allMethods.add(methodDeclaration);
        BlockStmt setNetworkNodeReference = methodDeclaration.getBody().orElseThrow(() -> new RuntimeException("No block statement"));

        List<List<NetworkNode>> partitionedNodes = ListUtils.partition(nodes, 20);

        for (int i = 0; i < partitionedNodes.size(); i++) {
            List<NetworkNode> nodesInPartition = partitionedNodes.get(i);
            MethodDeclaration m = generateInitMethodForPartition(i, nodesInPartition, setNetworkNodeReference);
            allMethods.add(m);
        }

        for (MethodDeclaration md : allMethods) {
            builder.append(md.toString());
            builder.append("\n");
        }
    }

    private MethodDeclaration generateInitMethodForPartition(int partitionIndex,
                                                             List<NetworkNode> nodeInPartition,
                                                             BlockStmt setNetworkNodeReferenceBody) {
        String initWithIndex = "initNode" + partitionIndex;

        CompilationUnit initialisationCompilationUnit = new CompilationUnit();
        initialisationCompilationUnit.setPackageDeclaration(ObjectTypeNodeCompiler.PACKAGE_NAME);
        initialisationCompilationUnit.addClass(ucFirst(initWithIndex));
        partitionedNodeInitialisationClasses.put(partitionIndex, initialisationCompilationUnit);

        BlockStmt setFieldStatementCall = StaticJavaParser.parseBlock(statementCall);
        setFieldStatementCall.findAll(MethodCallExpr.class, mc -> mc.getNameAsString().equals("initNodeN"))
                .forEach(n -> n.setName(new SimpleName(initWithIndex)));

        setFieldStatementCall.getStatements().forEach(setNetworkNodeReferenceBody::addStatement);

        MethodDeclaration initMethodWithIndex = new MethodDeclaration(nodeList(Modifier.publicModifier()),
                                                                      new VoidType(),
                                                                      initWithIndex
        );

        BlockStmt initBlockPerPartition = initMethodWithIndex.getBody().orElseThrow(() -> new RuntimeException("No"));
        generateInitBody(initBlockPerPartition, nodeInPartition, partitionIndex);

        return initMethodWithIndex;
    }

    private void generateInitBody(BlockStmt initBodyPerPartition,
                                  List<NetworkNode> subNodes,
                                  int partitionIndex) {

        CompilationUnit partitionedCompilationUnit = partitionedNodeInitialisationClasses.get(partitionIndex);
        PackageDeclaration partitionedClassPackage = (PackageDeclaration) partitionedCompilationUnit.getChildNodes().get(0);
        ClassOrInterfaceDeclaration partitionedClass = (ClassOrInterfaceDeclaration) partitionedCompilationUnit.getChildNodes().get(1);

        for (NetworkNode n : subNodes) {
            if (n instanceof CanInlineInANC) {

                String variableName;
                final ClassOrInterfaceType initMethodReturnType;
                if (n instanceof AlphaNode) {
                    variableName = getVariableName((AlphaNode) n);
                    initMethodReturnType = StaticJavaParser.parseClassOrInterfaceType(getVariableType((AlphaNode) n).getCanonicalName());
                } else {
                    variableName = getVariableName((Sink) n);
                    initMethodReturnType = StaticJavaParser.parseClassOrInterfaceType(getVariableType((Sink) n).getCanonicalName());
                }

                String initMethodName = String.format("init%s", variableName);

                // We'll need to partition the lambda creation as with too many the compiler crashes
                // We'll move it to a separated class
                MethodDeclaration initMethod = new MethodDeclaration(nodeList(Modifier.publicModifier(), Modifier.staticModifier()),
                                                                     initMethodName,
                                                                     initMethodReturnType,
                                                                     parametersFromAdditionalFields());
                Expression initExpressionForNode = ((CanInlineInANC) n).toANCInlinedForm();

                partitionedClass.addMember(initMethod);

                BlockStmt initMethodBlock = new BlockStmt();
                initMethodBlock.addStatement(new ReturnStmt(initExpressionForNode));
                initMethod.setBody(initMethodBlock);

                String callInitMethodAndAssignToFieldString = String.format("%s = %s.%s.%s(ctx);",
                                                                            variableName,
                                                                            partitionedClassPackage.getNameAsString(),
                                                                            partitionedClass.getNameAsString(),
                                                                            initMethodName);
                initBodyPerPartition.addStatement(parseStatement(callInitMethodAndAssignToFieldString));
            }
        }
    }

    private NodeList<Parameter> parametersFromAdditionalFields() {
        List<Parameter> parameters = additionalFields.stream()
                .map(f -> new Parameter(f.getCommonType(), f.getVariables().get(0).toString()))
                .collect(Collectors.toList());
        return nodeList(parameters);
    }
}
