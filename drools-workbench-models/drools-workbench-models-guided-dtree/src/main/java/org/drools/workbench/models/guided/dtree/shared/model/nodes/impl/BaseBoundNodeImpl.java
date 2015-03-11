/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.models.guided.dtree.shared.model.nodes.impl;

import org.drools.workbench.models.guided.dtree.shared.model.nodes.BoundNode;

public abstract class BaseBoundNodeImpl extends BaseNodeImpl implements BoundNode {

    private String binding;

    public BaseBoundNodeImpl() {
        //Errai marshalling
    }

    @Override
    public String getBinding() {
        return binding;
    }

    @Override
    public void setBinding( final String binding ) {
        this.binding = binding;
    }

    @Override
    public boolean isBound() {
        return !( binding == null || binding.isEmpty() );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseBoundNodeImpl)) return false;
        if (!super.equals(o)) return false;

        BaseBoundNodeImpl nodes = (BaseBoundNodeImpl) o;

        if (binding != null ? !binding.equals(nodes.binding) : nodes.binding != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (binding != null ? binding.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
