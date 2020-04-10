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
import java.util.Collection;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.config.CachedRuleEventListenerConfig;
import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.drools.core.config.StaticRuleConfig;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.rules.RuleEventListenerConfig;

import static org.kie.kogito.codegen.CodegenUtils.genericType;
import static org.kie.kogito.codegen.CodegenUtils.method;
import static org.kie.kogito.codegen.CodegenUtils.newObject;
import static org.kie.kogito.codegen.ConfigGenerator.callMerge;

public class RuleConfigGenerator {

    private static final String METHOD_EXTRACT_RULE_EVENT_LISTENER_CONFIG = "extract_ruleEventListenerConfig";
    private static final String METHOD_MERGE_RULE_EVENT_LISTENER_CONFIG = "merge_ruleEventListenerConfig";
    private static final String VAR_AGENDA_EVENT_LISTENERS = "agendaEventListeners";
    private static final String VAR_DEFAULT_RULE_EVENT_LISTENER_CONFIG = "defaultRuleEventListenerConfig";
    private static final String VAR_RULE_EVENT_LISTENER_CONFIGS = "ruleEventListenerConfigs";
    private static final String VAR_RULE_RUNTIME_EVENT_LISTENERS = "ruleRuntimeEventListeners";

    private List<BodyDeclaration<?>> members = new ArrayList<>();

    private DependencyInjectionAnnotator annotator;

    public ObjectCreationExpr newInstance() {
        if (annotator != null) {
            return new ObjectCreationExpr()
                    .setType(StaticRuleConfig.class.getCanonicalName())
                    .addArgument(new MethodCallExpr(METHOD_EXTRACT_RULE_EVENT_LISTENER_CONFIG));
        } else {
            return new ObjectCreationExpr()
                    .setType(StaticRuleConfig.class.getCanonicalName())
                    .addArgument(new NameExpr(VAR_DEFAULT_RULE_EVENT_LISTENER_CONFIG));
        }
    }
    public List<BodyDeclaration<?>> members() {

        if (annotator != null) {
            FieldDeclaration relcFieldDeclaration = annotator.withOptionalInjection(new FieldDeclaration().addVariable(new VariableDeclarator(genericType(annotator.multiInstanceInjectionType(), RuleEventListenerConfig.class), VAR_RULE_EVENT_LISTENER_CONFIGS)));
            members.add(relcFieldDeclaration);

            FieldDeclaration aelFieldDeclaration = annotator.withOptionalInjection(new FieldDeclaration().addVariable(new VariableDeclarator(genericType(annotator.multiInstanceInjectionType(), AgendaEventListener.class), VAR_AGENDA_EVENT_LISTENERS)));
            members.add(aelFieldDeclaration);

            FieldDeclaration rrelFieldDeclaration = annotator.withOptionalInjection(new FieldDeclaration().addVariable(new VariableDeclarator(genericType(annotator.multiInstanceInjectionType(), RuleRuntimeEventListener.class), VAR_RULE_RUNTIME_EVENT_LISTENERS)));
            members.add(rrelFieldDeclaration);

            members.add(generateExtractEventListenerConfigMethod());
            members.add(generateMergeEventListenerConfigMethod());
        } else {
            FieldDeclaration defaultRelcFieldDeclaration = new FieldDeclaration()
                    .setModifiers(Modifier.Keyword.PRIVATE)
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, RuleEventListenerConfig.class.getCanonicalName()), VAR_DEFAULT_RULE_EVENT_LISTENER_CONFIG, newObject(DefaultRuleEventListenerConfig.class)));
            members.add(defaultRelcFieldDeclaration);
        }

        return members;
    }

    public RuleConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    private MethodDeclaration generateExtractEventListenerConfigMethod() {
        BlockStmt body = new BlockStmt().addStatement(new ReturnStmt(
                new MethodCallExpr(new ThisExpr(), METHOD_MERGE_RULE_EVENT_LISTENER_CONFIG, NodeList.nodeList(
                        annotator.getMultiInstance(VAR_RULE_EVENT_LISTENER_CONFIGS),
                        annotator.getMultiInstance(VAR_AGENDA_EVENT_LISTENERS),
                        annotator.getMultiInstance(VAR_RULE_RUNTIME_EVENT_LISTENERS)
                ))
        ));

        return method(Modifier.Keyword.PRIVATE, RuleEventListenerConfig.class, METHOD_EXTRACT_RULE_EVENT_LISTENER_CONFIG, body);
    }

    private MethodDeclaration generateMergeEventListenerConfigMethod() {
        BlockStmt body = new BlockStmt().addStatement(new ReturnStmt(newObject(CachedRuleEventListenerConfig.class,
                callMerge(
                        VAR_RULE_EVENT_LISTENER_CONFIGS,
                        RuleEventListenerConfig.class, "agendaListeners",
                        VAR_AGENDA_EVENT_LISTENERS
                ),
                callMerge(
                        VAR_RULE_EVENT_LISTENER_CONFIGS,
                        RuleEventListenerConfig.class, "ruleRuntimeListeners",
                        VAR_RULE_RUNTIME_EVENT_LISTENERS
                )
        )));

        return method(Modifier.Keyword.PRIVATE, RuleEventListenerConfig.class, METHOD_MERGE_RULE_EVENT_LISTENER_CONFIG,
                NodeList.nodeList(
                        new Parameter().setType(genericType(Collection.class, RuleEventListenerConfig.class)).setName(VAR_RULE_EVENT_LISTENER_CONFIGS),
                        new Parameter().setType(genericType(Collection.class, AgendaEventListener.class)).setName(VAR_AGENDA_EVENT_LISTENERS),
                        new Parameter().setType(genericType(Collection.class, RuleRuntimeEventListener.class)).setName(VAR_RULE_RUNTIME_EVENT_LISTENERS)
                ),
                body);
    }

}
