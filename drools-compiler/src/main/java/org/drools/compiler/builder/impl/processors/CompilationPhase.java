/**
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
package org.drools.compiler.builder.impl.processors;

import java.util.Collection;

import org.kie.internal.builder.KnowledgeBuilderResult;

/**
 * Processes a PackageDescr and produces {@link KnowledgeBuilderResult}s.
 *
 * It usually analyzes a {@link org.drools.drl.ast.descr.PackageDescr}
 * and a {@link org.drools.compiler.compiler.PackageRegistry},
 * mutating them in place.
 *
 * This design originates from methods in {@link org.drools.compiler.builder.impl.KnowledgeBuilderImpl}
 * that have been moved to stand-alone classes, in order
 * to minimize the changes to the original code,
 * and it may change in the future.
 *
 */
public interface CompilationPhase {
    void process();

    Collection<? extends KnowledgeBuilderResult> getResults();
}
