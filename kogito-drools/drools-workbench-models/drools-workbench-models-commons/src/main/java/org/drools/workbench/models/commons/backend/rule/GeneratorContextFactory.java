/*
 * Copyright 2013 JBoss Inc
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
package org.drools.workbench.models.commons.backend.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for Generator Contexts
 */
public class GeneratorContextFactory {

    private List<GeneratorContext> contexts = new ArrayList<GeneratorContext>();

    public GeneratorContext newGeneratorContext() {
        final GeneratorContext gc = new GeneratorContext();
        contexts.add( gc );
        return gc;
    }

    public GeneratorContext newChildGeneratorContext( GeneratorContext parent ) {
        final GeneratorContext gc = new GeneratorContext( parent,
                                                          parent.getDepth() + 1,
                                                          parent.getOffset() + 1 );
        contexts.add( gc );
        return gc;
    }

    public List<GeneratorContext> getGeneratorContexts() {
        return contexts;
    }

}
