/*
 * Copyright 2010 JBoss Inc
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

package org.drools.common;

import org.drools.core.util.LinkedList;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;

public class ActivationGroupImpl
    implements
    ActivationGroup {
    private String           name;

    private final LinkedList list;

    public ActivationGroupImpl(final String name) {
        this.name = name;
        this.list = new LinkedList();
    }

    public String getName() {
        return this.name;
    }

    public void addActivation(final Activation activation) {
        final ActivationGroupNode node = new ActivationGroupNode( activation,
                                                                  this );
        activation.setActivationGroupNode( node );
        this.list.add( node );
    }

    public void removeActivation(final Activation activation) {
        final ActivationGroupNode node = activation.getActivationGroupNode();
        this.list.remove( node );
        activation.setActivationGroupNode( null );
    }

    public java.util.Iterator iterator() {
        return this.list.javaUtilIterator();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }

}
