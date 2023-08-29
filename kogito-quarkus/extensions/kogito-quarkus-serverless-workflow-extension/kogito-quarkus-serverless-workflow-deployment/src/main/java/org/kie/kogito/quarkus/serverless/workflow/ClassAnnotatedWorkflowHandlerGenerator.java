/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.quarkus.serverless.workflow;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import com.github.javaparser.ast.NodeList;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;

public abstract class ClassAnnotatedWorkflowHandlerGenerator implements WorkflowHandlerGenerator {

    @Override
    public Collection<WorkflowHandlerGeneratedFile> generateHandlerClasses(KogitoBuildContext context, IndexView index) {
        return index.getAnnotations(DotName.createSimple(getAnnotation().getCanonicalName())).stream().flatMap(a -> generateHandler(context, a)).collect(Collectors.toList());
    }

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract Stream<WorkflowHandlerGeneratedFile> generateHandler(KogitoBuildContext context, AnnotationInstance a);

    protected final com.github.javaparser.ast.type.Type fromClass(Type param) {
        switch (param.kind()) {
            case CLASS:
                return parseClassOrInterfaceType(param.asClassType().name().toString());
            case PRIMITIVE:
                return parseType(param.asPrimitiveType().name().toString());
            case PARAMETERIZED_TYPE:
                return parseClassOrInterfaceType(param.asParameterizedType().name().toString())
                        .setTypeArguments(NodeList.nodeList(param.asParameterizedType().arguments().stream().map(this::fromClass).collect(Collectors.toList())));
            default:
                throw new UnsupportedOperationException("Kind " + param.kind() + " is not supported");
        }
    }
}
