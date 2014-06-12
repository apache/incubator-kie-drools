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
package org.drools.workbench.models.commons.backend.rule.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for Generator Contexts
 */
public class LHSGeneratorContextFactory {

    private List<LHSGeneratorContext> contexts = new ArrayList<LHSGeneratorContext>();

    public LHSGeneratorContext newGeneratorContext() {
        final LHSGeneratorContext gc = new LHSGeneratorContext();
        contexts.add( gc );
        return gc;
    }

    public LHSGeneratorContext newChildGeneratorContext( final LHSGeneratorContext parent ) {
        final LHSGeneratorContext gc = new LHSGeneratorContext( parent,
                                                                parent.getDepth() + 1,
                                                                parent.getOffset() + 1 );
        contexts.add( gc );
        return gc;
    }

    public List<LHSGeneratorContext> getGeneratorContexts() {
        return contexts;
    }

}
