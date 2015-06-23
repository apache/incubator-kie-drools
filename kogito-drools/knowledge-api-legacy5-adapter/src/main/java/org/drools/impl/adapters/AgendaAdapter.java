/*
 * Copyright 2015 JBoss Inc
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

import org.drools.runtime.rule.ActivationGroup;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.RuleFlowGroup;

public class AgendaAdapter implements Agenda {

    private final org.kie.api.runtime.rule.Agenda delegate;

    public AgendaAdapter(org.kie.api.runtime.rule.Agenda delegate) {
        this.delegate = delegate;
    }

    public void clear() {
        delegate.clear();
    }

    public AgendaGroup getAgendaGroup(String name) {
        return new AgendaGroupAdapter(delegate.getAgendaGroup(name));
    }

    public ActivationGroup getActivationGroup(String name) {
        return new ActivationGroupAdapter(delegate.getActivationGroup(name));
    }

    public RuleFlowGroup getRuleFlowGroup(String name) {
        return new RuleFlowGroupAdapter(delegate.getRuleFlowGroup(name));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgendaAdapter && delegate.equals(((AgendaAdapter)obj).delegate);
    }
}
