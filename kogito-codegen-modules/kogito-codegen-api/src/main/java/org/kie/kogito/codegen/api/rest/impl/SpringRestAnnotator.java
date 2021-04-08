/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.api.rest.impl;

import java.util.Optional;
import java.util.stream.Stream;

import org.kie.kogito.codegen.api.rest.RestAnnotator;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

public class SpringRestAnnotator implements RestAnnotator {

    @Override
    public <T extends NodeWithAnnotations<?>> boolean isRestAnnotated(T node) {
        return Stream.of("PostMapping", "GetMapping", "PutMapping", "DeleteMapping")
                .map(node::getAnnotationByName)
                .anyMatch(Optional::isPresent);
    }

    @Override
    public <T extends NodeWithAnnotations<?>> Optional<String> getEndpointValue(T node) {
        Optional<AnnotationExpr> path = node.getAnnotationByName("PostMapping");
        return path
                .flatMap(p -> p.asNormalAnnotationExpr()
                        .getPairs()
                        .stream()
                        .filter(x -> "value".equals(x.getName().asString())).findFirst())
                .map(value -> value.getValue().asStringLiteralExpr().asString());
    }
}
