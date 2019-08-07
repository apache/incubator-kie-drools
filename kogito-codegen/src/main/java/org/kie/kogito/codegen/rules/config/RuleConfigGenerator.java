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

package org.kie.kogito.codegen.rules.config;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.drools.core.config.StaticRuleConfig;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.CodegenUtils;
import org.kie.kogito.rules.RuleEventListenerConfig;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class RuleConfigGenerator {

    private static final String DEFAULT_RULE_EVENT_LISTENER_CONFIG = "defaultRuleEventListenerConfig";
    
    private List<BodyDeclaration<?>> members = new ArrayList<>();
    
    private DependencyInjectionAnnotator annotator;

    public ObjectCreationExpr newInstance() {
        if (annotator!= null) {
            return new ObjectCreationExpr()
                    .setType(StaticRuleConfig.class.getCanonicalName())
                    .addArgument(new MethodCallExpr("extract_ruleEventListenerConfig"));
        } else {
            return new ObjectCreationExpr()
                .setType(StaticRuleConfig.class.getCanonicalName())
                .addArgument(new NameExpr(DEFAULT_RULE_EVENT_LISTENER_CONFIG));
        }
    }
    
    public List<BodyDeclaration<?>> members() {
        
        FieldDeclaration defaultRelcFieldDeclaration = new FieldDeclaration()
                .setStatic(true)
                .setModifiers(Keyword.PRIVATE)
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, RuleEventListenerConfig.class.getCanonicalName()), 
                                                    DEFAULT_RULE_EVENT_LISTENER_CONFIG,
                                                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, DefaultRuleEventListenerConfig.class.getCanonicalName()), NodeList.nodeList())));
        members.add(defaultRelcFieldDeclaration);
        
        if (annotator != null) {
            FieldDeclaration relcFieldDeclaration = new FieldDeclaration()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, new SimpleName(annotator.optionalInstanceInjectionType()), NodeList.nodeList(new ClassOrInterfaceType(null, RuleEventListenerConfig.class.getCanonicalName()))), "ruleEventListenerConfig"));
            annotator.withInjection(relcFieldDeclaration);
            
            members.add(relcFieldDeclaration);
            members.add(CodegenUtils.extractOptionalInjection(RuleEventListenerConfig.class.getCanonicalName(), "ruleEventListenerConfig", DEFAULT_RULE_EVENT_LISTENER_CONFIG, annotator));            
        }
        
        return members;
    }
    
    public RuleConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }
}
