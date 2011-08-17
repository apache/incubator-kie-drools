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

package org.drools.command.runtime.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.Variable;

@XmlAccessorType( XmlAccessType.NONE )
public class QueryCommand  implements GenericCommand<QueryResults> {

    private static final long serialVersionUID = 510l;

    @XmlAttribute(name = "out-identifier")
    private String outIdentifier;
    @XmlAttribute(required = true)
    private String name;
    
    @XmlElement
    private List<Object> arguments;

    public QueryCommand() {
    }
    
    public QueryCommand(String outIdentifier, String name, Object... arguments) {
        this.outIdentifier = outIdentifier;
        this.name = name;
        if ( arguments != null ) {
            this.arguments = Arrays.asList( arguments );
        } else {
            this.arguments = Collections.EMPTY_LIST;
        }
    }
    
    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Object> getArguments() {
        if (this.arguments == null) {
            this.arguments = Collections.emptyList();
        }
        return this.arguments;
    }
    public void setArguments(List<Object> arguments) {
        if ( arguments == null || arguments.isEmpty() ) {
            this.arguments = Collections.emptyList();
        }
        this.arguments = arguments;
    }

    public QueryResults execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        QueryResults results = null;
        if ( this.arguments == null || this.arguments.isEmpty() ) {
            this.arguments = Collections.emptyList();
        }
        
        for (int j = 0; j < arguments.size(); j++) {
            if (arguments.get(j) instanceof Variable) {
                arguments.set(j, Variable.v);
            }
        }

        
        results = ksession.getQueryResults( name, this.arguments.toArray() );
        
        if ( this.outIdentifier != null ) {
            ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult().getResults().put( this.outIdentifier, results );
        }

        return results;
    }
}
