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

import static com.github.javaparser.StaticJavaParser.parse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.BodyDeclarationComparator;
import org.kie.kogito.Addons;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.config.ProcessConfigGenerator;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ConfigGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGenerator.class);
    private static final String RESOURCE = "/class-templates/config/ApplicationConfigTemplate.java";

    private DependencyInjectionAnnotator annotator;
    private ProcessConfigGenerator processConfig;
    private RuleConfigGenerator ruleConfig;
    
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
    
    public ConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr()
                .setType(targetCanonicalName);

    }

    private Expression processConfig() {
        return processConfig == null ? new NullLiteralExpr() : processConfig.newInstance();
    }
    
    private Expression ruleConfig() {
        return ruleConfig == null ? new NullLiteralExpr() : ruleConfig.newInstance();
    }
    
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit =
                parse(this.getClass().getResourceAsStream(RESOURCE))
                        .setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration cls = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new RuntimeException("ApplicationConfig template class not found"));
        
        if (processConfig != null) {
            for (BodyDeclaration<?> member : processConfig.members()) {
                cls.addMember(member);
            }
        }
        
        if (ruleConfig != null) {
            for (BodyDeclaration<?> member : ruleConfig.members()) {
                cls.addMember(member);
            }
        }
        
        
        // add found addons
        BlockStmt addonsMethodBody = new BlockStmt();
        MethodDeclaration addonsMethod = new MethodDeclaration()
                .setName("addons")
                .setType(Addons.class.getCanonicalName())
                .setModifiers(Keyword.PUBLIC)
                .setBody(addonsMethodBody);
        cls.addMember(addonsMethod);
        
        MethodCallExpr asList = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList");
        ReturnStmt returnStmt = new ReturnStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, Addons.class.getCanonicalName()), NodeList.nodeList(asList)));
        
        addonsMethodBody.addStatement(returnStmt);
        try {
            Enumeration<URL> urls = classLoader.getResources("META-INF/kogito.addon");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String addon = StringUtils.readFileAsString(new InputStreamReader(url.openStream()));
                asList.addArgument(new StringLiteralExpr(addon));
            }
        } catch (IOException e) {
            LOGGER.warn("Unexpected exception during loading of kogito.addon files", e);
        }
        
        MethodDeclaration initMethod = new MethodDeclaration()
            .addModifier(Keyword.PUBLIC)
            .setName("init")
            .setType(void.class)
            .setBody(new BlockStmt()
                     .addStatement(new AssignExpr(new NameExpr("processConfig"), processConfig(), AssignExpr.Operator.ASSIGN))
                     .addStatement(new AssignExpr(new NameExpr("ruleConfig"), ruleConfig(), AssignExpr.Operator.ASSIGN)));
            
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
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
    
    public String generatedFilePath() {
        return sourceFilePath;
    }

    public void withClassLoader(ClassLoader projectClassLoader) {
        this.classLoader = projectClassLoader;
    }
}
