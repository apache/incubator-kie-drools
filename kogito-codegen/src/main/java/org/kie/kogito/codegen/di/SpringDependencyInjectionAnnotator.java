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


package org.kie.kogito.codegen.di;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.BlockStmt;


public class SpringDependencyInjectionAnnotator implements DependencyInjectionAnnotator {

    @Override
    public void withApplicationComponent(NodeWithAnnotations<?> node) {
        node.addAnnotation("org.springframework.stereotype.Component");
    }
    
    @Override
    public void withNamedApplicationComponent(NodeWithAnnotations<?> node, String name) {        
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.stereotype.Component"), new StringLiteralExpr(name)));
    }

    @Override
    public void withSingletonComponent(NodeWithAnnotations<?> node) {
        node.addAnnotation("org.springframework.stereotype.Component");
    }
    
    @Override
    public void withNamedSingletonComponent(NodeWithAnnotations<?> node, String name) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.stereotype.Component"), new StringLiteralExpr(name)));
    }

    @Override
    public void withInjection(NodeWithAnnotations<?> node) {
        node.addAnnotation("org.springframework.beans.factory.annotation.Autowired");
    }

    @Override
    public void withNamedInjection(NodeWithAnnotations<?> node, String name) {
        node.addAnnotation("org.springframework.beans.factory.annotation.Autowired");
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Qualifier"), new StringLiteralExpr(name)));
    }
    
    @Override
    public void withOptionalInjection(NodeWithAnnotations<?> node) {
        node.addAnnotation(new NormalAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Autowired"), NodeList.nodeList(new MemberValuePair("required", new BooleanLiteralExpr(false)))));
    }
    
    @Override
    public void withIncomingMessage(NodeWithAnnotations<?> node, String channel) {
        node.addAnnotation(new NormalAnnotationExpr(new Name("org.springframework.kafka.annotation.KafkaListener"), NodeList.nodeList(new MemberValuePair("topics", new StringLiteralExpr(channel)))));
    }

    @Override
    public void withOutgoingMessage(NodeWithAnnotations<?> node, String channel) {
        // currently no-op
        
    }
    
    @Override
    public void withMessageProducer(MethodCallExpr produceMethod, String channel, Expression event) {
        produceMethod.addArgument(new StringLiteralExpr(channel)).addArgument(event);
    }

    @Override
    public String multiInstanceInjectionType() {
        return List.class.getCanonicalName();
    }
    
    @Override
    public String optionalInstanceInjectionType() {
        return Optional.class.getCanonicalName();
    }

    @Override
    public String applicationComponentType() {
        return "org.springframework.stereotype.Component";
    }
    
    @Override
    public String emitterType(String dataType) {
        return "org.springframework.kafka.core.KafkaTemplate<String, "+ dataType + ">";
    }

    @Override
    public MethodDeclaration withInitMethod(Expression... expression) {
        BlockStmt body = new BlockStmt();
        for (Expression exp : expression) {
            body.addStatement(exp);
        }
        return new MethodDeclaration()
                .addModifier(Keyword.PUBLIC)
                .setName("init")
                .setType(void.class)
                .addAnnotation("javax.annotation.PostConstruct")
                .setBody(body);
    }
    

    @Override
    public Expression optionalInstanceExists(String fieldName) {
        return new MethodCallExpr(new NameExpr(fieldName), "isPresent");
    }

    @Override
    public void withConfigInjection(String configKey, NodeWithAnnotations<?> node) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Value"), new StringLiteralExpr("${" + configKey + "}")));
        
    }

    @Override
    public void withConfigInjection(String configKey, String defaultValue, NodeWithAnnotations<?> node) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Value"), new StringLiteralExpr("${" + configKey + ":" + defaultValue + "}")));
        
    }
    
}
