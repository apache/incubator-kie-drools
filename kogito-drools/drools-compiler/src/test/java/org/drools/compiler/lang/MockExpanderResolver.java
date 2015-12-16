/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang;

import java.util.HashMap;
import java.util.Map;

public class MockExpanderResolver
    implements
        ExpanderResolver {

    private final Map          resolveCalls = new HashMap();
    private final MockExpander expander     = new MockExpander();

    public Expander get(final String name,
                        final String config) {
        this.resolveCalls.put( name,
                               config );
        return this.expander;
    }

    /**
     * Check if it was called.
     */
    public boolean checkCalled(final String name) {
        return this.resolveCalls.containsKey( name );
    }

    public String getConfigFor(final String name) {
        return (String) this.resolveCalls.get( name );
    }

    public boolean checkExpanded(final String patternOriginal) {
        return this.expander.checkPattern( patternOriginal );
    }
}
