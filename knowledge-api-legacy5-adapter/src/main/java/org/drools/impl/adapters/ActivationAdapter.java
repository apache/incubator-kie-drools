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
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.PropagationContext;
import org.kie.api.runtime.rule.Match;

import java.util.ArrayList;
import java.util.List;

public class ActivationAdapter implements Activation {

    private final Match delegate;

    public ActivationAdapter(Match delegate) {
        this.delegate = delegate;
    }

    public Rule getRule() {
        return new RuleAdapter(delegate.getRule());
    }

    public PropagationContext getPropagationContext() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public List<? extends FactHandle> getFactHandles() {
        List<FactHandle> result = new ArrayList<FactHandle>();
        for (org.kie.api.runtime.rule.FactHandle fh : delegate.getFactHandles()) {
            result.add(new FactHandleAdapter(fh));
        }
        return result;
    }

    public List<Object> getObjects() {
        return delegate.getObjects();
    }

    public List<String> getDeclarationIDs() {
        return delegate.getDeclarationIds();
    }

    public Object getDeclarationValue(String declarationId) {
        return delegate.getDeclarationValue(declarationId);
    }

    public boolean isActive() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivationAdapter && delegate.equals(((ActivationAdapter)obj).delegate);
    }
}
