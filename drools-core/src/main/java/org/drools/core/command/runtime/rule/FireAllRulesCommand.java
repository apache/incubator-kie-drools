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

package org.drools.core.command.runtime.rule;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FireAllRulesCommand implements ExecutableCommand<Integer>, IdentifiableResult {

    @XmlAttribute
    private int          max          = -1;

    @XmlTransient
    // TODO: make sure that all drools AgendaFilter implementations are serializable
    private AgendaFilter agendaFilter = null;

    @XmlAttribute(name="out-identifier")
    private String       outIdentifier;

    public FireAllRulesCommand() {
    }

    public FireAllRulesCommand(String outIdentifer) {
        this.outIdentifier = outIdentifer;
    }

    public FireAllRulesCommand(int max) {
        this.max = max;
    }

    public FireAllRulesCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public FireAllRulesCommand(AgendaFilter agendaFilter, int max) {
        this(agendaFilter);
        this.max = max;
    }

    public FireAllRulesCommand(String outIdentifier,
                               int max,
                               AgendaFilter agendaFilter) {
        this(agendaFilter, max);
        this.outIdentifier = outIdentifier;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Integer execute(Context context) {
        KieSession ksession = ((RegistryContext)context).lookup( KieSession.class );
        int fired;
        if ( max != -1 && agendaFilter != null ) {
            fired = ((StatefulKnowledgeSessionImpl) ksession).fireAllRules( agendaFilter, max );
        } else if ( max != -1 ) {
            fired = ksession.fireAllRules( max );
        } else if ( agendaFilter != null ) {
            fired = ((StatefulKnowledgeSessionImpl) ksession).fireAllRules( agendaFilter );
        } else {
            fired = ksession.fireAllRules();
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier, fired);
        }
        return fired;
    }

    public String toString() {
        if ( max != -1 && agendaFilter != null ) {
            return "session.fireAllRules( " + agendaFilter + ", " + max + " );";
        } else if ( max != -1 ) {
            return "session.fireAllRules( " + max + " );";
        } else if ( agendaFilter != null ) {
            return "session.fireAllRules( " + agendaFilter + " );";
        } else {
            return "session.fireAllRules();";
        }
    }

}
