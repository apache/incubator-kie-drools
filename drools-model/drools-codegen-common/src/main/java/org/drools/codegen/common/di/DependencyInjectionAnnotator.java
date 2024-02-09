/*
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
package org.drools.codegen.common.di;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

/**
 * Generic abstraction for dependency injection annotations that allow to
 * use different frameworks based needs.
 * <p>
 * Currently in scope
 *
 * <ul>
 * <li>CDI</li>
 * <li>Spring</li>
 * </ul>
 */
public interface DependencyInjectionAnnotator {

    /**
     * Annotates the given node with an annotation to produce a DI instance of the node target class, e.g. Produces,
     * Bean. This is used by configuration classes in the DI, like a factory method.
     * 
     * @param node
     * @param isDefault indicates if the bean instance is created only if there are not any other bean of this type
     *        already declared in the application, e.g DefaultBean
     */
    <T extends NodeWithAnnotations<?>> T withProduces(T node, boolean isDefault);

    /**
     * Annotates given node with name annotation e.g. Named, Qualifier
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withNamed(T node, String name);

    /**
     * Annotates given node with application level annotations e.g. ApplicationScoped, Component
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withApplicationComponent(T node);

    /**
     * Annotates given node with application level annotations e.g. ApplicationScoped, Component
     * additionally adding name to it
     *
     * @param node node to be annotated
     * @param name name to be assigned to given node
     */
    <T extends NodeWithAnnotations<?>> T withNamedApplicationComponent(T node, String name);

    /**
     * Annotates given node with singleton level annotations e.g. Singleton, Component
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withSingletonComponent(T node);

    /**
     * Annotates given node with singleton level annotations e.g. Singleton, Component
     * additionally adding name to it
     *
     * @param node node to be annotated
     * @param name name to be assigned to given node
     */
    <T extends NodeWithAnnotations<?>> T withNamedSingletonComponent(T node, String name);

    /**
     * Annotates given node with injection annotations e.g. Inject, Autowire
     *
     * @param node node to be annotated
     * @boolean forceLazyInit use lazy initialization (for those container that applies)
     */
    <T extends NodeWithAnnotations<?>> T withInjection(T node, boolean forceLazyInit);

    /**
     * Annotates given node with injection annotations e.g. Inject, Autowire
     *
     * @param node node to be annotated
     */
    default <T extends NodeWithAnnotations<?>> T withInjection(T node) {
        return withInjection(node, false);
    }

    /**
     * Annotates given node with injection annotations e.g. Inject, Autowire
     * additionally adding name to it
     *
     * @param node node to be annotated
     * @param name name to be assigned to given node
     */
    <T extends NodeWithAnnotations<?>> T withNamedInjection(T node, String name);

    /**
     * Annotates given node with optional injection annotations e.g. Inject, Autowire
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withOptionalInjection(T node);

    /**
     * Annotates given node with incoming message that it should consume from
     *
     * @param node node to be annotated
     * @param channel name of the channel messages should be consumer from
     */
    <T extends NodeWithAnnotations<?>> T withIncomingMessage(T node, String channel);

    /**
     * Annotates given node with outgoing message that it should send to
     *
     * @param node node to be annotated
     * @param channel name of the channel messages should be send to
     */
    <T extends NodeWithAnnotations<?>> T withOutgoingMessage(T node, String channel);

    /**
     * Annotates given node with configuration parameter injection
     *
     * @param node node to be annotated
     * @param configKey name of the configuration property to be injected
     */
    <T extends NodeWithAnnotations<?>> T withConfigInjection(T node, String configKey);

    /**
     * Annotates given node with configuration parameter injection with default value
     *
     * @param node node to be annotated
     * @param configKey name of the configuration property to be injected
     * @param defaultValue value to be used in case there is no config parameter defined
     */
    <T extends NodeWithAnnotations<?>> T withConfigInjection(T node, String configKey, String defaultValue);

    /**
     * Annotates and enhances method used to produce messages
     *
     * @param produceMethod method to be annotated
     * @param channel channel on which messages should be produced
     * @param event actual data to be send
     */
    MethodCallExpr withMessageProducer(MethodCallExpr produceMethod, String channel, Expression event);

    /**
     * Annotates given node with set of roles to enforce security
     *
     * @param node node to be annotated
     * @param roles roles that are allowed
     */
    default <T extends NodeWithAnnotations<?>> T withSecurityRoles(T node, String[] roles) {
        if (roles != null && roles.length > 0) {
            List<Expression> rolesExpr = new ArrayList<>();

            for (String role : roles) {
                rolesExpr.add(new StringLiteralExpr(role.trim()));
            }

            node.addAnnotation(new SingleMemberAnnotationExpr(new Name("jakarta.annotation.security.RolesAllowed"), new ArrayInitializerExpr(NodeList.nodeList(rolesExpr))));
        }
        return node;
    }

    /**
     * Returns type that allows to inject optional instances of the same type
     *
     * @return fully qualified class name
     */
    String optionalInstanceInjectionType();

    /**
     * Creates an expression that represents optional instance for given field
     *
     * @param fieldName name of the field that should be considered optional
     * @return complete expression for optional instance
     */
    Expression optionalInstanceExists(String fieldName);

    /**
     * Creates an expression that returns instance for given optional field
     *
     * @param fieldName name of the optional field that should be accessed
     * @return complete expression for optional instance
     */
    default Expression getOptionalInstance(String fieldName) {
        return new MethodCallExpr(new NameExpr(fieldName), "get");
    }

    /**
     * Returns type that allows to inject multiple instances of the same type
     *
     * @return fully qualified class name
     */
    String multiInstanceInjectionType();

    /**
     * Creates an expression that returns a list of instances for given multi instance field
     *
     * @param fieldName name of the multi field that should be accessed
     * @return complete expression for multi instance
     */
    Expression getMultiInstance(String fieldName);

    /**
     * Returns type that allows to mark instance as application component e.g. ApplicationScoped, Component
     *
     * @return fully qualified class name
     */
    String applicationComponentType();

    /**
     * Returns type to be used as message emitter
     *
     * @param dataType type of the data produces by the emitter
     * @return fully qualified class name
     */
    String emitterType(String dataType);

    /**
     * Annotates given node with a initializing annotation e.g. Startup
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withEagerStartup(T node);

    /**
     * Annotates given node with factory class annotations e.g. Configuration for Spring Boot
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withFactoryClass(T node);

    /**
     * Annotates given node with factory method annotations e.g. Produces, Bean
     *
     * @param node node to be annotated
     */
    <T extends NodeWithAnnotations<?>> T withFactoryMethod(T node);

    default <T extends NodeWithAnnotations<?>> T withTagAnnotation(T node, NodeList<MemberValuePair> attributes) {
        return node;
    }
}
