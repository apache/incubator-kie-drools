/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common;

import org.drools.model.Declaration;
import org.drools.model.PatternDSL;

public final class DroolsGenuineMetadata<A> implements DroolsMetadata<A, A> {

    private final Declaration<A> variableDeclaration;
    private final PatternDSL.PatternDef<A> pattern;

    DroolsGenuineMetadata(Declaration<A> variableDeclaration, PatternDSL.PatternDef<A> pattern) {
        this.variableDeclaration = variableDeclaration;
        this.pattern = pattern;
    }

    public DroolsGenuineMetadata<A> substitute(PatternDSL.PatternDef<A> newPattern) {
        return DroolsMetadata.ofGenuine(variableDeclaration, newPattern);
    }

    @Override
    public A extract(A container) {
        return container;
    }

    @Override
    public Declaration<A> getVariableDeclaration() {
        return variableDeclaration;
    }

    @Override
    public PatternDSL.PatternDef<A> getPattern() {
        return pattern;
    }
}
