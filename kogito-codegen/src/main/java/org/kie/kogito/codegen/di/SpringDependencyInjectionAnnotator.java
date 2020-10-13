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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parse;


public class SpringDependencyInjectionAnnotator implements DependencyInjectionAnnotator {

    @Override
    public <T extends NodeWithAnnotations<?>> T withNamed(T node, String name) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Qualifier"), new StringLiteralExpr(name)));
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withApplicationComponent(T node) {
        node.addAnnotation("org.springframework.stereotype.Component");
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withNamedApplicationComponent(T node, String name) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.stereotype.Component"), new StringLiteralExpr(name)));
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withSingletonComponent(T node) {
        return withApplicationComponent(node);
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withNamedSingletonComponent(T node, String name) {
        return withNamedApplicationComponent(node, name);
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withInjection(T node, boolean lazy) {
        node.addAnnotation("org.springframework.beans.factory.annotation.Autowired");
        if (lazy) {
            node.addAnnotation("org.springframework.context.annotation.Lazy");
        }
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withNamedInjection(T node, String name) {
        return withNamed(withInjection(node), name);
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withOptionalInjection(T node) {
        node.addAnnotation(new NormalAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Autowired"), NodeList.nodeList(new MemberValuePair("required", new BooleanLiteralExpr(false)))));
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withIncomingMessage(T node, String channel) {
        node.addAnnotation(new NormalAnnotationExpr(new Name("org.springframework.kafka.annotation.KafkaListener"), NodeList.nodeList(new MemberValuePair("topics", new StringLiteralExpr(channel)))));
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withOutgoingMessage(T node, String channel) {
        // currently no-op
        return node;
    }

    @Override
    public MethodCallExpr withMessageProducer(MethodCallExpr produceMethod, String channel, Expression event) {
        produceMethod.addArgument(new StringLiteralExpr(channel)).addArgument(event);
        return produceMethod;
    }

    @Override
    public String optionalInstanceInjectionType() {
        return Optional.class.getCanonicalName();
    }

    @Override
    public Expression optionalInstanceExists(String fieldName) {
        return new MethodCallExpr(new NameExpr(fieldName), "isPresent");
    }

    @Override
    public String multiInstanceInjectionType() {
        return Collection.class.getCanonicalName();
    }

    @Override
    public Expression getMultiInstance(String fieldName) {
        return new ConditionalExpr(
                new BinaryExpr(new NameExpr(fieldName), new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS),
                new NameExpr(fieldName),
                new MethodCallExpr(new TypeExpr(new ClassOrInterfaceType(null, Collections.class.getCanonicalName())), "emptyList")
        );
    }

    @Override
    public String applicationComponentType() {
        return "org.springframework.stereotype.Component";
    }

    @Override
    public String emitterType(String dataType) {
        return "org.springframework.kafka.core.KafkaTemplate<String, " + dataType + ">";
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withConfigInjection(T node, String configKey) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Value"), new StringLiteralExpr("${" + configKey + ":#{null}}")));
        return node;
    }

    @Override
    public <T extends NodeWithAnnotations<?>> T withConfigInjection(T node, String configKey, String defaultValue) {
        node.addAnnotation(new SingleMemberAnnotationExpr(new Name("org.springframework.beans.factory.annotation.Value"), new StringLiteralExpr("${" + configKey + ":" + defaultValue + "}")));
        return node;
    }

    @Override
    public String objectMapperInjectorSource(String packageName) {
        CompilationUnit clazz = parse(
                this.getClass().getResourceAsStream("/class-templates/rules/KogitoSpringObjectMapper.java"));
        clazz.setPackageDeclaration( packageName );
        return clazz.toString();
    }

    /**
     * no-op, Spring beans are not lazy by default. 
     * @param node node to be annotated
     * @return
     */
    @Override
    public <T extends NodeWithAnnotations<?>> T withEagerStartup(T node) {
        return node;
    }
}
