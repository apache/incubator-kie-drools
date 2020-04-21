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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
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
import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.decision.DecisionModels;

public class DecisionContainerGenerator extends AbstractApplicationSection {

    private static final String TEMPLATE_JAVA = "/class-templates/DMNApplicationClassDeclTemplate.java";

    private String applicationCanonicalName;
    private final Path basePath;
    private final Collection<DMNModel> models;

    public DecisionContainerGenerator(String applicationCanonicalName, Path basePath, Collection<DMNModel> models) {
        super("DecisionModels", "decisionModels", DecisionModels.class);
        this.applicationCanonicalName = applicationCanonicalName;
        this.basePath = basePath;
        this.models = models;
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
        for (DMNModel model : models) {
            Path sourcePath = Paths.get(model.getResource().getSourcePath());
            Path relativizedPath = basePath.relativize(sourcePath);
            String resourcePath = "/" + relativizedPath.toString().replace(File.separatorChar, '/');
            MethodCallExpr getResAsStream = new MethodCallExpr(new FieldAccessExpr(applicationClass.getNameAsExpression(), "class"), "getResourceAsStream").addArgument(new StringLiteralExpr(resourcePath));
            ObjectCreationExpr isr = new ObjectCreationExpr().setType(inputStreamReaderClass).addArgument(getResAsStream);
            Optional<FieldDeclaration> dmnRuntimeField = typeDeclaration.getFieldByName("dmnRuntime");
            Optional<Expression> initalizer = dmnRuntimeField.flatMap(x -> x.getVariable(0).getInitializer());
            if (initalizer.isPresent()) {
                initalizer.get().asMethodCallExpr().addArgument(isr);
            } else {
                throw new RuntimeException("The template " + TEMPLATE_JAVA + " has been modified.");
            }
        }
        return typeDeclaration;
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
