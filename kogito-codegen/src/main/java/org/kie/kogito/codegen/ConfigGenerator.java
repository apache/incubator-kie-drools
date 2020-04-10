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

package org.kie.kogito.codegen;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.kie.kogito.Addons;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.config.ProcessConfigGenerator;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.CodegenUtils.method;
import static org.kie.kogito.codegen.CodegenUtils.newObject;

public class ConfigGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGenerator.class);
    private static final String RESOURCE = "/class-templates/config/ApplicationConfigTemplate.java";

    private DependencyInjectionAnnotator annotator;
    private ProcessConfigGenerator processConfig;
    private RuleConfigGenerator ruleConfig;
    private DecisionConfigGenerator decisionConfig;
    
    private String packageName;
    private final String sourceFilePath;
    private final String targetTypeName;
    private final String targetCanonicalName;
    
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    public ConfigGenerator(String packageName) {
        this.packageName = packageName;
        this.targetTypeName = "ApplicationConfig";
        this.targetCanonicalName = this.packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
    }

    public ConfigGenerator withProcessConfig(ProcessConfigGenerator cfg) {
        this.processConfig = cfg;
        if (this.processConfig != null) {
            this.processConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withRuleConfig(RuleConfigGenerator cfg) {
        this.ruleConfig = cfg;
        if (this.ruleConfig != null) {
            this.ruleConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withDecisionConfig(DecisionConfigGenerator cfg) {
        this.decisionConfig = cfg;
        if (this.decisionConfig != null) {
            this.decisionConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr()
                .setType(targetCanonicalName);
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit =
                parse(this.getClass().getResourceAsStream(RESOURCE))
                        .setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration cls = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new RuntimeException("ApplicationConfig template class not found"));

        if (processConfig != null) {
            processConfig.members().forEach(cls::addMember);
        }

        if (ruleConfig != null) {
            ruleConfig.members().forEach(cls::addMember);
        }

        if (decisionConfig != null) {
            decisionConfig.members().forEach(cls::addMember);
        }

        // add found addons
        cls.addMember(generateAddonsMethod());

        // init method
        MethodDeclaration initMethod = generateInitMethod();
        if (useInjection()) {
            annotator.withSingletonComponent(cls);
            initMethod.addAnnotation("javax.annotation.PostConstruct");
        } else {
            cls.addConstructor(Keyword.PUBLIC).setBody(new BlockStmt().addStatement(new MethodCallExpr(new ThisExpr(), "init")));
        }
        cls.addMember(initMethod);

        cls.getMembers().sort(new BodyDeclarationComparator());
        return compilationUnit;
    }

    private MethodDeclaration generateAddonsMethod() {
        MethodCallExpr asListOfAddons = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList");
        try {
            Enumeration<URL> urls = classLoader.getResources("META-INF/kogito.addon");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String addon = StringUtils.readFileAsString(new InputStreamReader(url.openStream()));
                asListOfAddons.addArgument(new StringLiteralExpr(addon));
            }
        } catch (IOException e) {
            LOGGER.warn("Unexpected exception during loading of kogito.addon files", e);
        }

        BlockStmt body = new BlockStmt().addStatement(new ReturnStmt(
                newObject(Addons.class, asListOfAddons)
        ));

        return method(Keyword.PUBLIC, Addons.class, "addons", body);
    }

    private MethodDeclaration generateInitMethod() {
        BlockStmt body = new BlockStmt()
                .addStatement(new AssignExpr(new NameExpr("processConfig"), newProcessConfigInstance(), AssignExpr.Operator.ASSIGN))
                .addStatement(new AssignExpr(new NameExpr("ruleConfig"), newRuleConfigInstance(), AssignExpr.Operator.ASSIGN))
                .addStatement(new AssignExpr(new NameExpr("decisionConfig"), newDecisionConfigInstance(), AssignExpr.Operator.ASSIGN));

        return method(Keyword.PUBLIC, void.class, "init", body);
    }

    private Expression newProcessConfigInstance() {
        return processConfig == null ? new NullLiteralExpr() : processConfig.newInstance();
    }

    private Expression newRuleConfigInstance() {
        return ruleConfig == null ? new NullLiteralExpr() : ruleConfig.newInstance();
    }

    private Expression newDecisionConfigInstance() {
        return decisionConfig == null ? new NullLiteralExpr() : decisionConfig.newInstance();
    }

    protected boolean useInjection() {
        return this.annotator != null;
    }

    public String generatedFilePath() {
        return sourceFilePath;
    }

    public void withClassLoader(ClassLoader projectClassLoader) {
        this.classLoader = projectClassLoader;
    }

    public static MethodCallExpr callMerge(String configsName, Class<?> configToListenersScope, String configToListenersIdentifier, String listenersName) {
        return new MethodCallExpr(null, "merge", NodeList.nodeList(
                new NameExpr(configsName),
                new MethodReferenceExpr(
                        new TypeExpr(new ClassOrInterfaceType(null, configToListenersScope.getCanonicalName())),
                        null,
                        configToListenersIdentifier
                ),
                new NameExpr(listenersName)
        ));
    }

}
