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
package org.drools.codegen.common.rest.impl;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import org.drools.codegen.common.rest.RestAnnotator;

import java.util.Optional;
import java.util.stream.Stream;

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
