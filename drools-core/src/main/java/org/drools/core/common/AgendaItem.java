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

package org.drools.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.kie.api.runtime.rule.FactHandle;

public interface AgendaItem<T extends ModedAssertion<T>> extends Activation<T> {

    void setPropagationContext(PropagationContext context);

    void setSalience(int salience);

    void setActivationFactHandle( InternalFactHandle factHandle );

    RuleAgendaItem getRuleAgendaItem();

    void removeAllBlockersAndBlocked(InternalAgenda agenda);

    void removeBlocked(LogicalDependency<SimpleMode> dep);

    TerminalNode getTerminalNode();

    String toExternalForm();

    boolean isCanceled();

    void cancel();

    List<FactHandle> getFactHandles();

    Runnable getCallback();
    void setCallback(Runnable callback);

    default List<FactHandle> getFactHandles(Tuple tuple) {
        FactHandle[] factHandles = tuple.toFactHandles();
        List<FactHandle> list = new ArrayList<FactHandle>( factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(factHandle);
            }
        }
        return Collections.unmodifiableList( list );
    }

    default List<Object> getObjectsDeep(LeftTuple entry) {
        List<Object> list = new ArrayList<Object>();
        while ( entry != null ) {
            if ( entry.getFactHandle() != null ) {
                Object o = ((InternalFactHandle) entry.getFactHandle()).getObject();
                if (!(o instanceof QueryElementFactHandle)) {
                    list.add(o);
                    list.addAll( entry.getAccumulatedObjects() );
                }
            }
            entry = entry.getParent();
        }
        return list;
    }

    default List<Object> getObjects(Tuple tuple) {
        FactHandle[] factHandles = tuple.toFactHandles();
        List<Object> list = new ArrayList<Object>(factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(o);
            }
        }
        return Collections.unmodifiableList(list);
    }
}
