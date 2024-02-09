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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.WindowNode;
import org.drools.base.rule.IndexableConstraint;
import org.drools.core.common.PropagationContext;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.util.index.AlphaRangeIndex;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.mvelcompiler.util.TypeUtils.toJPType;
import static org.drools.util.StringUtils.md5Hash;

// As AssertCompiler and ModifyCompiler classes are quite similar except for the method they propagate in the Rete (assert vs modify) they share a common class
public abstract class PropagatorCompilerHandler extends AbstractCompilerHandler {

    /**
     * This states if there is at least 1 set of hashed alpha nodes in the network
     */
    protected final boolean alphaNetContainsHashedField;
    protected final String factClassName;

    protected static final String FACT_HANDLE_PARAM_NAME = "handle";
    protected static final String PROP_CONTEXT_PARAM_NAME = "context";
    protected static final String WORKING_MEMORY_PARAM_NAME = "wm";
    protected static final String MODIFY_PREVIOUS_TUPLE_PARAM_NAME = "modifyPreviousTuples";
    protected static final String LOCAL_FACT_VAR_NAME = "fact";

    private Class<?> fieldType;

    protected BlockStmt allStatements = new BlockStmt();
    protected Deque<Node> currentStatement = new ArrayDeque<>();
    protected List<MethodDeclaration> extractedMethods = new ArrayList<>();

    protected PropagatorCompilerHandler(boolean alphaNetContainsHashedField, String factClassName) {
        this.alphaNetContainsHashedField = alphaNetContainsHashedField;
        this.factClassName = factClassName;
        currentStatement.push(allStatements);
    }

    protected abstract Statement propagateMethod(Sink sink);

    protected abstract NodeList<Parameter> methodParameters();

    protected abstract NodeList<Expression> arguments();

    @Override
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {
        // we only need to create a reference to the object, not handle, if there is a hashed alpha in the network
        if (alphaNetContainsHashedField) {
            // example of what this will look like
            // ExampleFact fact = (ExampleFact) handle.getObject();

            ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(factClassName);
            ExpressionStmt factVariable = localVariableWithCastInitializer(type, LOCAL_FACT_VAR_NAME, new MethodCallExpr(new NameExpr(FACT_HANDLE_PARAM_NAME), "getObject"));

            getCurrentBlockStatement().addStatement(factVariable);
        }
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        getCurrentBlockStatement().addStatement((propagateMethod(betaNode)));
    }

    @Override
    public void startWindowNode(WindowNode windowNode) {
        getCurrentBlockStatement().addStatement((propagateMethod(windowNode)));
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        getCurrentBlockStatement().addStatement(propagateMethod(leftInputAdapterNode));
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {

        IfStmt ifStatement = parseStatement("if (CONSTRAINT.isAllowed(handle, wm)) { }").asIfStmt();

        replaceNameExpr(ifStatement, "CONSTRAINT", getVariableName(alphaNode));

        getCurrentBlockStatement().addStatement(ifStatement);

        currentStatement.push(ifStatement);
    }

    @Override
    public void endNonHashedAlphaNode(AlphaNode alphaNode) {
        currentStatement.pop();
    }

    @Override
    public void startHashedAlphaNodes(IndexableConstraint indexableConstraint) {
        final ReadAccessor fieldExtractor = indexableConstraint.getFieldExtractor();
        fieldType = fieldExtractor.getExtractToClass();
        BlockStmt currentBlockStatement = getCurrentBlockStatement();

        final SwitchStmt switchStmt;
        final Statement nullCheck;
        if (canInlineValue(fieldType)) {

            String switchVariableName = "switchVar";
            ExpressionStmt switchVariable = localVariableWithCastInitializer(toJPType(fieldType),
                                                                             switchVariableName,
                                                                             parseExpression("readAccessor.getValue(fact)"));

            currentBlockStatement.addStatement(switchVariable);
            switchStmt = new SwitchStmt().setSelector(new NameExpr(switchVariableName));

            if (fieldType.isPrimitive()) {
                nullCheck = new BlockStmt().addStatement(switchStmt);
            } else {
                nullCheck = new IfStmt()
                        .setCondition(new BinaryExpr(new NameExpr(switchVariableName), new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS))
                        .setThenStmt(new BlockStmt().addStatement(switchStmt));
            }
        } else { // Hashable but not inlinable

            String localVariableName = "NodeId";

            ExpressionStmt expressionStmt = localVariableWithCastInitializer(parseType("java.lang.Integer"),
                                                                             localVariableName,
                                                                             parseExpression("ToNodeId.get(readAccessor.getValue(fact))"));

            currentBlockStatement.addStatement(expressionStmt);

            switchStmt = new SwitchStmt().setSelector(new MethodCallExpr(new NameExpr(localVariableName), "intValue", nodeList()));

            // ensure that the value is present in the node map
            nullCheck = new IfStmt()
                    .setCondition(new BinaryExpr(new NameExpr(localVariableName), new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS))
                    .setThenStmt(new BlockStmt().addStatement(switchStmt));
        }

        currentBlockStatement.addStatement(nullCheck);
        this.currentStatement.push(switchStmt);
    }

    protected boolean canInlineValue(Class<?> fieldType) {
        return Stream.of(String.class, Integer.class, int.class).anyMatch(c -> c.isAssignableFrom(fieldType));
    }

    @Override
    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        SwitchEntry newSwitchEntry = new SwitchEntry();

        if (canInlineValue(fieldType)) {
            final Expression quotedHashedValue;
            if (hashedValue instanceof String) {
                quotedHashedValue = new StringLiteralExpr((String) hashedValue);
            } else if (hashedValue instanceof Long) {
                quotedHashedValue = new LongLiteralExpr((Long) hashedValue);
            } else {
                quotedHashedValue = new IntegerLiteralExpr((Integer) hashedValue);
            }
            newSwitchEntry.setLabels(nodeList(quotedHashedValue));
        } else {
            newSwitchEntry.setLabels(nodeList(new IntegerLiteralExpr(hashedAlpha.getId())));
        }
        addNewSwitchEntryToStack(newSwitchEntry);
    }

    @Override
    public void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        addBreakStatement(getLastSwitchEntry());
        this.currentStatement.pop();
    }

    @Override
    public void startRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        String rangeIndexVariableName = getRangeIndexVariableName(alphaRangeIndex, getMinIdFromRangeIndex(alphaRangeIndex));
        String matchingResultVariableName = rangeIndexVariableName + "_result";
        String matchingNodeVariableName = matchingResultVariableName + "_node";

        ExpressionStmt matchingResultVariable = localVariable(parseType("java.util.Collection<org.drools.core.reteoo.AlphaNode>"),
                                                              matchingResultVariableName,
                                                              new MethodCallExpr(new NameExpr(rangeIndexVariableName),
                                                                                 "getMatchingAlphaNodes",
                                                                                 nodeList(new MethodCallExpr(new NameExpr(FACT_HANDLE_PARAM_NAME), "getObject"))));

        final BlockStmt currentBlockStatement = getCurrentBlockStatement();

        currentBlockStatement.addStatement(matchingResultVariable);

        BlockStmt body = new BlockStmt();
        ForEachStmt forEachStmt = new ForEachStmt(new VariableDeclarationExpr(parseType("org.drools.core.reteoo.AlphaNode"), matchingNodeVariableName),
                                                  new NameExpr(matchingResultVariableName), body);

        currentBlockStatement.addStatement(forEachStmt);

        SwitchStmt switchStatement = new SwitchStmt().setSelector(new MethodCallExpr(new NameExpr(matchingNodeVariableName), "getId"));
        this.currentStatement.push(switchStatement);
        body.addStatement(switchStatement);
    }

    @Override
    public void startRangeIndexedAlphaNode(AlphaNode alphaNode) {
        SwitchEntry switchEntry = new SwitchEntry().setLabels(nodeList(new IntegerLiteralExpr(alphaNode.getId())));
        addNewSwitchEntryToStack(switchEntry);
    }

    private void addNewSwitchEntryToStack(SwitchEntry switchEntry) {
        SwitchStmt currentSwitch = (SwitchStmt) currentStatement.getFirst();
        BlockStmt block = new BlockStmt();
        block.setParentNode(switchEntry);
        this.currentStatement.push(block);
        switchEntry.setStatements(nodeList(block));
        currentSwitch.getEntries().add(switchEntry);
    }

    @Override
    public void endRangeIndexedAlphaNode(AlphaNode alphaNode) {
        addBreakStatement(getLastSwitchEntry());
        this.currentStatement.pop();
    }

    public SwitchEntry getLastSwitchEntry() {
        return this.currentStatement
                .getFirst()
                .findAncestor(SwitchEntry.class)
                .orElseThrow(() -> new CouldNotCreateAlphaNetworkCompilerException("No switch entry to break found"));
    }

    @Override
    public void endRangeIndex(AlphaRangeIndex alphaRangeIndex) {
        this.currentStatement.pop();
    }

    private void addBreakStatement(SwitchEntry switchEntry) {
        switchEntry.getStatements().add(new BreakStmt());
    }

    public BlockStmt getCurrentBlockStatement() {
        if (currentStatement.getFirst() instanceof IfStmt) {
            return (BlockStmt) ((IfStmt) currentStatement.getFirst()).getThenStmt();
        }
        return (BlockStmt) currentStatement.getFirst();
    }

    protected abstract String propagateMethodName();

    public String emitCode() {

        MethodDeclaration propagateMethod =
                new MethodDeclaration()
                        .setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.FINAL)
                        .setType(new VoidType())
                        .setName(propagateMethodName())
                        .setParameters(methodParameters());

        BlockStmt body = new BlockStmt();
        propagateMethod.setBody(body);

        body.addStatement(StaticJavaParser.parseStatement(String.format("if(logger.isDebugEnabled()) {\n" +
                                                                                "            logger.debug(\"%s on compiled alpha network {} {} {}\", handle, context, wm);\n" +
                                                                                "        }\n", propagateMethodName())));

        postProcessAllStatements();

        for (Statement s : allStatements.getStatements()) {
            body.addStatement(s);
        }

        StringBuilder allCodeGenerated = new StringBuilder();
        allCodeGenerated.append(propagateMethod.toString());
        allCodeGenerated.append(NEWLINE);

        for(MethodDeclaration md : extractedMethods) {
            allCodeGenerated.append(md.toString());
            allCodeGenerated.append(NEWLINE);
            allCodeGenerated.append(NEWLINE);
        }
        return allCodeGenerated.toString();
    }

    private void postProcessAllStatements() {
        partitionSwitchEntries();
    }

    private void partitionSwitchEntries() {
        this.allStatements
                .findAll(SwitchEntry.class)
                .forEach(this::extractMethod);
    }

    private void extractMethod(SwitchEntry switchEntry) {

        String label = switchEntry.getLabels().stream().map(Node::toString).collect(Collectors.joining());

        SwitchStmt switchStatement = switchEntry.findAncestor(SwitchStmt.class)
                .orElseThrow(() -> new CouldNotCreateAlphaNetworkCompilerException("SwitchEntry without SwitchStatement"));

        int index = switchStatement.getEntries().indexOf(switchEntry);
        String selectorString = switchStatement.getSelector().toString();

        String newMethodName = String.format("extractedPropagated_%s_%d", md5Hash(selectorString), index);

        // First statement is actual block, second statement is break
        BlockStmt switchEntryStatements = (BlockStmt) switchEntry.getStatements().get(0);
        MethodDeclaration extractedMethod = new MethodDeclaration()
                .setModifiers(nodeList(Modifier.publicModifier()))
                .setName(newMethodName)
                .setParameters(methodParameters())
                .setType(new VoidType())
                .setBody(switchEntryStatements);

        extractedMethod.setComment(new LineComment(selectorString + " " + label));

        MethodCallExpr callExtractedMethod = new MethodCallExpr()
                .setName(newMethodName)
                .setArguments(arguments());

        switchEntry.setStatements(nodeList(
                new ExpressionStmt(callExtractedMethod),
                new BreakStmt()
        ));

        extractedMethods.add(extractedMethod);
    }

    public ClassOrInterfaceType modifyPreviousTuplesType() {
        return StaticJavaParser.parseClassOrInterfaceType(ModifyPreviousTuples.class.getCanonicalName());
    }

    public ClassOrInterfaceType factHandleType() {
        return StaticJavaParser.parseClassOrInterfaceType(InternalFactHandle.class.getCanonicalName());
    }

    public ClassOrInterfaceType propagationContextType() {
        return StaticJavaParser.parseClassOrInterfaceType(PropagationContext.class.getName());
    }

    public ClassOrInterfaceType reteEvaluatorType() {
        return StaticJavaParser.parseClassOrInterfaceType(ReteEvaluator.class.getName());
    }

    //  type variableName = (type) sourceObject.methodName();
    protected ExpressionStmt localVariableWithCastInitializer(Type type, String variableName, MethodCallExpr source) {
        return new ExpressionStmt(
                new VariableDeclarationExpr(
                        new VariableDeclarator(type, variableName,
                                               new CastExpr(type, source))));
    }

    //  type variableName = (type) sourceObject.methodName();
    protected ExpressionStmt localVariable(Type type, String variableName, MethodCallExpr source) {
        return new ExpressionStmt(
                new VariableDeclarationExpr(
                        new VariableDeclarator(type, variableName,
                                               source)));
    }
}
