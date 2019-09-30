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

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

/**
 * Generic abstraction for dependency injection annotations that allow to
 * use different frameworks based needs.
 * 
 * Currently in scope 
 * 
 * <ul>
 *  <li>CDI</li>
 *  <li>Spring</li>
 * </ul>
 *
 */
public interface DependencyInjectionAnnotator {

    /**
     * Annotates given node with application level annotations e.g. ApplicationScoped, Component
     * @param node node to be annotated
     */
    void withApplicationComponent(NodeWithAnnotations<?> node);
    
    /**
     * Annotates given node with application level annotations e.g. ApplicationScoped, Component
     * additionally adding name to it
     * @param node node to be annotated
     * @param name name to be assigned to given node
     */
    void withNamedApplicationComponent(NodeWithAnnotations<?> node, String name);
    
    /**
     * Annotates given node with singleton level annotations e.g. Singleton, Component
     * @param node node to be annotated
     */
    void withSingletonComponent(NodeWithAnnotations<?> node);
    
    /**
     * Annotates given node with singleton level annotations e.g. Singleton, Component
     * additionally adding name to it
     * @param node node to be annotated
     * @param name name to be assigned to given node
     */
    void withNamedSingletonComponent(NodeWithAnnotations<?> node, String name);
    
    /**
     * Annotates given node with injection annotations e.g. Inject, Autowire
     * @param node node to be annotated
     */
    void withInjection(NodeWithAnnotations<?> node);
    
    /**
     * Annotates given node with injection annotations e.g. Inject, Autowire
     * additionally adding name to it
     * @param node node to be annotated
     * @param name name to be assigned to given node
     */
    void withNamedInjection(NodeWithAnnotations<?> node, String name);
    
    /**
     * Annotates given node with optional injection annotations e.g. Inject, Autowire    
     * @param node node to be annotated
     */
    void withOptionalInjection(NodeWithAnnotations<?> node);
    
    /**
     * Annotates given node with incoming message that it should consume from
     * @param node node to be annotated
     * @param channel name of the channel messages should be consumer from
     */
    void withIncomingMessage(NodeWithAnnotations<?> node, String channel);
    
    /**
     * Annotates given node with outgoing message that it should send to
     * @param node node to be annotated
     * @param channel name of the channel messages should be send to
     */
    void withOutgoingMessage(NodeWithAnnotations<?> node, String channel);
    
    /**
     * Annotates given node with configuration parameter injection
     * @param configKey name of the configuration property to be injected
     * @param node node to be annotated
     */
    void withConfigInjection(String configKey, NodeWithAnnotations<?> node);
    
    /**
     * Annotates given node with configuration parameter injection with default value
     * @param configKey name of the configuration property to be injected
     * @param defaultValue value to be used in case there is no config parameter defined
     * @param node node to be annotated
     */
    void withConfigInjection(String configKey, String defaultValue, NodeWithAnnotations<?> node);
    
    /**
     * Annotates and enhances method used to produce messages 
     * @param produceMethod method to be annotated
     * @param channel channel on which messages should be produced
     * @param event actual data to be send
     */
    void withMessageProducer(MethodCallExpr produceMethod, String channel, Expression event);

    /**
     * Create initialization method with given expression as body 
     * @param expression body of the init method
     * @return complete initialization method declaration
     */
    MethodDeclaration withInitMethod(Expression... expression);
    
    /**
     * Creates an expression that represents optional instance for given field
     * @param fieldName name of the field that should be considered optional
     * @return complete expression for optional instance
     */
    Expression optionalInstanceExists(String fieldName);
    
    /**
     * Returns type that allows to inject multiple instances of the same type
     * @return fully qualified class name
     */
    String multiInstanceInjectionType();
    
    /**
     * Returns type that allows to inject optional instances of the same type
     * @return fully qualified class name
     */
    String optionalInstanceInjectionType();
    
    /**
     * Returns type that allows to mark instance as application component e.g. ApplicationScoped, Component
     * @return fully qualified class name
     */
    String applicationComponentType();
    
    /**
     * Returns type to be used as message emitter
     * @param dataType type of the data produces by the emitter
     * @return fully qualified class name 
     */
    String emitterType(String dataType);
        
}
