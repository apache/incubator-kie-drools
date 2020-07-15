/*
  * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.decision;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.IoUtils;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DmnExecutionIdSupplier;

import static org.kie.kogito.codegen.CodegenUtils.newObject;

public class DecisionContainerGenerator extends AbstractApplicationSection {

    private static final String TEMPLATE_JAVA = "/class-templates/DMNApplicationClassDeclTemplate.java";

    private String applicationCanonicalName;
    private final List<DMNResource> resources;
    private AddonsConfig addonsConfig = AddonsConfig.DEFAULT;

    public DecisionContainerGenerator(String applicationCanonicalName, List<DMNResource> resources) {
        super("DecisionModels", "decisionModels", DecisionModels.class);
        this.applicationCanonicalName = applicationCanonicalName;
        this.resources = resources;
    }

    public DecisionContainerGenerator withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        //        FieldDeclaration dmnRuntimeField = new FieldDeclaration().addModifier(Modifier.Keyword.STATIC)
        //                                                                 .addVariable(new VariableDeclarator().setType(DMNRuntime.class.getCanonicalName())
        //                                                                                                      .setName("dmnRuntime")
        //                                                                                                      .setInitializer(new MethodCallExpr("org.kie.dmn.kogito.rest.quarkus.DMNKogitoQuarkus.createGenericDMNRuntime")));
        //        ClassOrInterfaceDeclaration cls = super.classDeclaration();
        //        cls.addModifier(Modifier.Keyword.STATIC);
        //        cls.addMember(dmnRuntimeField);
        //
        //        MethodDeclaration getDecisionMethod = new MethodDeclaration().setName("getDecision")
        //                                                                     .setType(Decision.class.getCanonicalName())
        //                                                                     .addParameter(new Parameter(StaticJavaParser.parseType(String.class.getCanonicalName()), "namespace"))
        //                                                                     .addParameter(new Parameter(StaticJavaParser.parseType(String.class.getCanonicalName()), "name"))
        //        ;
        //        cls.addMember(getDecisionMethod);
        CompilationUnit clazz = StaticJavaParser.parse(this.getClass().getResourceAsStream(TEMPLATE_JAVA));
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) clazz.getTypes().get(0);
        ClassOrInterfaceType applicationClass = StaticJavaParser.parseClassOrInterfaceType(applicationCanonicalName);
        ClassOrInterfaceType inputStreamReaderClass = StaticJavaParser.parseClassOrInterfaceType(java.io.InputStreamReader.class.getCanonicalName());
        for (DMNResource resource : resources) {
            MethodCallExpr getResAsStream = getReadResourceMethod( applicationClass, resource );
            ObjectCreationExpr isr = new ObjectCreationExpr().setType(inputStreamReaderClass).addArgument(getResAsStream);
            Optional<FieldDeclaration> dmnRuntimeField = typeDeclaration.getFieldByName("dmnRuntime");
            Optional<Expression> initalizer = dmnRuntimeField.flatMap(x -> x.getVariable(0).getInitializer());
            if (initalizer.isPresent()) {
                initalizer.get().asMethodCallExpr().addArgument(isr);
            } else {
                throw new RuntimeException("The template " + TEMPLATE_JAVA + " has been modified.");
            }
        }
        if (addonsConfig.useTracing()) {
            VariableDeclarator execIdSupplierVariable = typeDeclaration.getFieldByName("execIdSupplier")
                    .map(x -> x.getVariable(0))
                    .orElseThrow(() -> new RuntimeException("Can't find \"execIdSupplier\" field in " + TEMPLATE_JAVA));
            execIdSupplierVariable.setInitializer(newObject(DmnExecutionIdSupplier.class));
        }
        return typeDeclaration;
    }

    private MethodCallExpr getReadResourceMethod( ClassOrInterfaceType applicationClass, DMNResource resource ) {
        String source = resource.getDmnModel().getResource().getSourcePath();
        if (resource.getPath().toString().endsWith( ".jar" )) {
            return new MethodCallExpr(
                      new MethodCallExpr( new NameExpr( IoUtils.class.getCanonicalName() + ".class" ), "getClassLoader" ),
                    "getResourceAsStream").addArgument(new StringLiteralExpr(source));
        }
        
        Path relativizedPath = resource.getPath().relativize(Paths.get(source));
        String resourcePath = "/" + relativizedPath.toString().replace( File.separatorChar, '/');
        return new MethodCallExpr(new FieldAccessExpr(applicationClass.getNameAsExpression(), "class"), "getResourceAsStream")
                .addArgument(new StringLiteralExpr(resourcePath));
    }

    @Override
    protected boolean useApplication() {
        return false;
    }

    @Override
    public List<Statement> setupStatements() {
        return Collections.singletonList(
                new IfStmt(
                        new BinaryExpr(
                                new MethodCallExpr(new MethodCallExpr(null, "config"), "decision"),
                                new NullLiteralExpr(),
                                BinaryExpr.Operator.NOT_EQUALS
                        ),
                        new BlockStmt().addStatement(new ExpressionStmt(new MethodCallExpr(
                                new NameExpr("decisionModels"), "init", NodeList.nodeList(new ThisExpr())
                        ))),
                        null
                )
        );
    }

}
