/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.util.LinkedList;
import org.drools.core.spi.Activation;

public class ActivationGroupImpl
    implements
    InternalActivationGroup {
    private final String                          name;

    private final LinkedList<ActivationGroupNode> list;

    private final InternalAgenda                  agenda;
    
    private long triggeredForRecency;

    public ActivationGroupImpl(InternalAgenda agenda, String name) {
        this.agenda = agenda;
        this.name = name;
        this.list = new LinkedList();
        this.triggeredForRecency = -1;
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
        agenda.clearAndCancelActivationGroup( name );
    }

    public void reset() {
        list.clear();
    }

    public LinkedList<ActivationGroupNode> getList() {
        return list;
    }

    public long getTriggeredForRecency() {
        return triggeredForRecency;
    }

    public void setTriggeredForRecency(long executedForRecency) {
        this.triggeredForRecency = executedForRecency;
    }

}
