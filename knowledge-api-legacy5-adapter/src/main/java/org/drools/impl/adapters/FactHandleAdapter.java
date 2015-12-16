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

import org.drools.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FactHandleAdapter implements org.drools.runtime.rule.FactHandle {

    private final FactHandle delegate;

    public FactHandleAdapter(FactHandle delegate) {
        this.delegate = delegate;
    }

    public String toExternalForm() {
        return delegate.toExternalForm();
    }

    public FactHandle getDelegate() {
        return delegate;
    }

    public static List<org.drools.runtime.rule.FactHandle> adaptFactHandles(Collection<FactHandle> factHandles) {
        List<org.drools.runtime.rule.FactHandle> result = new ArrayList<org.drools.runtime.rule.FactHandle>();
        for (FactHandle factHandle : factHandles) {
            result.add(new FactHandleAdapter(factHandle));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FactHandleAdapter && delegate.equals(((FactHandleAdapter)obj).delegate);
    }
}
