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

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.event.rule.ActivationUnMatchListener;

import java.util.List;

public interface AgendaItem<T extends ModedAssertion<T>> extends Activation<T> {

    void setPropagationContext(PropagationContext context);

    void setSalience(int salience);

    void setActivationFactHandle( InternalFactHandle factHandle );

    RuleAgendaItem getRuleAgendaItem();

    void removeAllBlockersAndBlocked(InternalAgenda agenda);

    void removeBlocked(LogicalDependency<SimpleMode> dep);

    TerminalNode getTerminalNode();

    ActivationUnMatchListener getActivationUnMatchListener();

    void setActivationUnMatchListener(ActivationUnMatchListener activationUnMatchListener);

    String toExternalForm();

    boolean isCanceled();

    void cancel();

    List<FactHandle> getFactHandles();
}
