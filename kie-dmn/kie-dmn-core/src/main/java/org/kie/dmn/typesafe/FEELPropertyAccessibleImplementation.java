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
package org.kie.dmn.typesafe;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodWithStringBody;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.feel.util.EvalHelper;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.util.StringUtils.ucFirst;

public class FEELPropertyAccessibleImplementation {

    static class InvalidTemplateException extends DMNTypeSafeException {

        public InvalidTemplateException(String message) {
            super(message);
        }
    }

    CompilationUnit methodTemplate;

    List<DMNDeclaredField> fields;

    TypeDefinition typeDefinition;

    public FEELPropertyAccessibleImplementation(List<DMNDeclaredField> fields, TypeDefinition typeDefinition) {
        this.fields = fields;
        this.typeDefinition = typeDefinition;
    }

    public List<MethodDefinition> getMethods() {
        List<MethodDefinition> allMethods = new ArrayList<>();

        methodTemplate = getMethodTemplate();

        allMethods.add(getFeelPropertyDefinition());
        allMethods.add(setFeelPropertyDefinition());
        allMethods.add(fromMap());
        if (containsCompositeCollection()) {
            allMethods.add(processCompositeCollection());
        }
        allMethods.add(allFeelProperties());

        return allMethods;
    }

    private boolean containsCompositeCollection() {
        return fields.stream().anyMatch(DMNDeclaredField::isCompositeCollection);
    }

    private MethodDefinition getFeelPropertyDefinition() {

        MethodDeclaration getFEELProperty = cloneMethodTemplate("getFEELProperty");

        SwitchStmt firstSwitch = getFEELProperty.findFirst(SwitchStmt.class)
                .orElseThrow(() -> new InvalidTemplateException("Missing Switch Statement in getFEELProperty template"));

        firstSwitch.setComment(null);

        List<SwitchEntry> collect = fields.stream().map(this::toGetPropertySwitchEntry).collect(Collectors.toList());

        SwitchEntry defaultSwitchStmt = firstSwitch
                .findFirst(SwitchEntry.class, sw -> sw.getLabels().isEmpty())
                .orElseThrow(() -> new InvalidTemplateException("Missing Default Switch Statement in getFEELProperty template"));

        collect.add(defaultSwitchStmt);

        firstSwitch.setEntries(nodeList(collect));

        String body = getFEELProperty.getBody().orElseThrow(() -> new InvalidTemplateException("Empty body in getFeelProperty clone"))
                .toString();

        MethodWithStringBody getFeelPropertyDefinition =
                new MethodWithStringBody("getFEELProperty", EvalHelper.PropertyValueResult.class.getCanonicalName(), body)
                        .addParameter(String.class.getCanonicalName(), "property");

        addOverrideAnnotation(getFeelPropertyDefinition);

        return getFeelPropertyDefinition;
    }

    private void addOverrideAnnotation(MethodWithStringBody md) {
        md.addAnnotation("Override");
    }

    private MethodDeclaration cloneMethodTemplate(String methodName) {
        return methodTemplate.findFirst(MethodDeclaration.class, mc -> mc.getNameAsString().equals(methodName))
                .orElseThrow(() -> new InvalidTemplateException(String.format("Missing method in template: %s", methodName)))
                .clone();
    }

    private SwitchEntry toGetPropertySwitchEntry(DMNDeclaredField fieldDefinition) {
        ReturnStmt returnStmt = new ReturnStmt();
        MethodCallExpr mc = StaticJavaParser.parseExpression(EvalHelper.PropertyValueResult.class.getCanonicalName() + ".ofValue()");
        String accessorName = fieldDefinition.overriddenGetterName().orElse(getAccessorName(fieldDefinition, "get"));
        mc.addArgument(new MethodCallExpr(new ThisExpr(), accessorName));
        returnStmt.setExpression(mc);
        return new SwitchEntry(nodeList(new StringLiteralExpr(fieldDefinition.getOriginalMapKey())), SwitchEntry.Type.STATEMENT_GROUP, nodeList(returnStmt));
    }

    private MethodDefinition setFeelPropertyDefinition() {

        MethodDeclaration setFEELProperty = cloneMethodTemplate("setFEELProperty");

        SwitchStmt firstSwitch = setFEELProperty.findFirst(SwitchStmt.class)
                .orElseThrow(() -> new InvalidTemplateException("Missing switch statement in setFEELProperty"));

        firstSwitch.setComment(null);

        List<SwitchEntry> collect = fields.stream().map(this::toSetPropertySwitchEntry).collect(Collectors.toList());

        firstSwitch.setEntries(nodeList(collect));

        BlockStmt body = setFEELProperty.getBody().orElseThrow(() -> new InvalidTemplateException("Empty body in setFEELProperty"));

        if (typeDefinition instanceof AbstractDMNSetType) {
            body.addStatement(0, new ExpressionStmt(StaticJavaParser.parseExpression("definedKeySet.add(property)")));
        }

        MethodWithStringBody setFeelPropertyDefinition = new MethodWithStringBody("setFEELProperty", "void", body.toString())
                .addParameter(String.class.getCanonicalName(), "property")
                .addParameter(Object.class.getCanonicalName(), "value");

        addOverrideAnnotation(setFeelPropertyDefinition);

        return setFeelPropertyDefinition;
    }

    private SwitchEntry toSetPropertySwitchEntry(DMNDeclaredField fieldDefinition) {

        String accessorName = fieldDefinition.overriddenSetterName().orElse(getAccessorName(fieldDefinition, "set"));
        MethodCallExpr setMethod = new MethodCallExpr(new ThisExpr(), accessorName);
        setMethod.addArgument(new CastExpr(StaticJavaParser.parseType(fieldDefinition.getObjectType()), new NameExpr("value")));

        ExpressionStmt setStatement = new ExpressionStmt();
        setStatement.setExpression(setMethod);

        NodeList<Expression> labels = nodeList(new StringLiteralExpr(fieldDefinition.getOriginalMapKey()));
        NodeList<Statement> statements = nodeList(setStatement, new ReturnStmt());
        return new SwitchEntry(labels, SwitchEntry.Type.STATEMENT_GROUP, statements);
    }

    private MethodDefinition fromMap() {

        MethodDeclaration allFeelProperties = cloneMethodTemplate("fromMap");

        BlockStmt originalStatements = allFeelProperties.getBody()
                .orElseThrow(() -> new InvalidTemplateException("Missing body in allFeelProperties"));

        BlockStmt simplePropertyBLock = (BlockStmt) originalStatements.getStatement(0);
        BlockStmt pojoPropertyBlock = (BlockStmt) originalStatements.getStatement(1);
        BlockStmt collectionsCompositePropertyBlock = (BlockStmt) originalStatements.getStatement(2);
        BlockStmt collectionsBasic = (BlockStmt) originalStatements.getStatement(3);

        List<Statement> allStatements = fields.stream().map(f -> f.createFromMapEntry(simplePropertyBLock,
                                                                                      pojoPropertyBlock,
                                                                                      collectionsCompositePropertyBlock,
                                                                                      collectionsBasic))
                .collect(Collectors.toList());

        BlockStmt body = new BlockStmt(nodeList(allStatements));

        if (typeDefinition instanceof AbstractDMNSetType) {
            body.addStatement("values.keySet().stream().forEach(key -> definedKeySet.add(key));");
        }

        MethodWithStringBody setFeelProperty = new MethodWithStringBody("fromMap", "void", body.toString());
        setFeelProperty.addParameter("java.util.Map<String, Object>", "values");

        addOverrideAnnotation(setFeelProperty);

        return setFeelProperty;
    }

    private MethodDefinition processCompositeCollection() {

        MethodDeclaration processCompositeCollection = cloneMethodTemplate("processCompositeCollection");

        BlockStmt body = processCompositeCollection.getBody().orElseThrow(() -> new InvalidTemplateException("Missing body in generated method"));

        MethodWithStringBody processCompositeCollectionDefinition = new MethodWithStringBody("processCompositeCollection", "void", body.toString());
        processCompositeCollectionDefinition.addParameter("java.util.Collection", "destCol");
        processCompositeCollectionDefinition.addParameter("java.util.Collection", "srcCol");
        processCompositeCollectionDefinition.addParameter("Class<?>", "baseClass");

        return processCompositeCollectionDefinition;
    }

    private CompilationUnit getMethodTemplate() {
        InputStream resourceAsStream = this.getClass()
                .getResourceAsStream("/org/kie/dmn/core/impl/DMNTypeSafeTypeTemplate.java");
        return StaticJavaParser.parse(resourceAsStream);
    }

    private MethodWithStringBody allFeelProperties() {

        MethodDeclaration allFeelProperties = cloneMethodTemplate("allFEELProperties");

        MethodCallExpr putExpression = allFeelProperties.findFirst(MethodCallExpr.class,
                                                                   mc -> mc.getNameAsString().equals("put"))
                .orElseThrow(() -> new InvalidTemplateException("Missing put method in allFEELProperties"));

        List<Statement> collect = fields.stream().map(fieldDefinition -> toResultPut(putExpression, fieldDefinition)).collect(Collectors.toList());

        if (typeDefinition instanceof AbstractDMNSetType) {
            collect.add(new ExpressionStmt(StaticJavaParser.parseExpression("result.entrySet().removeIf(entry -> java.util.Objects.isNull(entry.getValue()) && !definedKeySet.contains(entry.getKey()))")));
        }

        BlockStmt newBlockStatement = new BlockStmt(nodeList(collect));

        putExpression.getParentNode().ifPresent(p -> p.replace(newBlockStatement));

        BlockStmt body = allFeelProperties.getBody().orElseThrow(() -> new InvalidTemplateException("Missing body in generated method"));

        MethodWithStringBody allFEELProperties = new MethodWithStringBody(
                "allFEELProperties",
                "java.util.Map<String, Object>",
                body.toString()
        );

        addOverrideAnnotation(allFEELProperties);
        return allFEELProperties;
    }

    private ExpressionStmt toResultPut(MethodCallExpr putExpression, DMNDeclaredField fieldDefinition) {
        MethodCallExpr clone = putExpression.clone();

        String fieldName = fieldDefinition.getOriginalMapKey();

        String accessorName = fieldDefinition.overriddenGetterName().orElse(getAccessorName(fieldDefinition, "get"));

        clone.findAll(StringLiteralExpr.class, se -> se.asString().equals("<PROPERTY_NAME>"))
                .forEach(s -> s.replace(new StringLiteralExpr(fieldName)));

        clone.findAll(MethodCallExpr.class, se -> se.getNameAsString().equals("getPropertyName"))
                .forEach(s -> s.setName(accessorName));

        return new ExpressionStmt(clone);
    }

    private String getAccessorName(FieldDefinition fieldDefinition, String get) {
        return get + ucFirst(fieldDefinition.getFieldName());
    }
}
