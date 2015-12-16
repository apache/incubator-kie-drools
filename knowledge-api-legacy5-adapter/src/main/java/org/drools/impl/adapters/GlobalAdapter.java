/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.impl.adapters;

import org.kie.api.definition.rule.Global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GlobalAdapter implements org.drools.definition.rule.Global {

    private final Global delegate;

    public GlobalAdapter(Global delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getName();
    }

    public String getType() {
        return delegate.getType();
    }

    public static List<org.drools.definition.rule.Global> adaptGlobals(Collection<org.kie.api.definition.rule.Global> globals) {
        List<org.drools.definition.rule.Global> result = new ArrayList<org.drools.definition.rule.Global>();
        for (org.kie.api.definition.rule.Global global : globals) {
            result.add(new GlobalAdapter(global));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GlobalAdapter && delegate.equals(((GlobalAdapter)obj).delegate);
    }
}
