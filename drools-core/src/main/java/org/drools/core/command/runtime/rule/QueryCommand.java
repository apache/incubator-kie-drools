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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.core.QueryResultsImpl;
import org.drools.core.command.IdentifiableResult;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType( XmlAccessType.NONE )
public class QueryCommand implements ExecutableCommand<QueryResults>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute(name = "out-identifier")
    private String outIdentifier;
    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute(required = false)
    private Boolean showFactHandleMaps = true;

    @XmlAttribute(required = false)
    private Boolean showResultMaps = true;

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

    public QueryCommand(String outIdentifier,
                        String name,
                        boolean showFactHandleMaps,
                        boolean showResultMaps,
                        Object... arguments) {
        this.showFactHandleMaps = showFactHandleMaps;
        this.showResultMaps = showResultMaps;
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

    public Boolean getShowFactHandleMaps() {
        return showFactHandleMaps;
    }

    public void setShowFactHandleMaps(Boolean showFactHandleMaps) {
        this.showFactHandleMaps = showFactHandleMaps;
    }

    public Boolean getShowResultMaps() {
        return showResultMaps;
    }

    public void setShowResultMaps(Boolean showResultMaps) {
        this.showResultMaps = showResultMaps;
    }

    public QueryResults execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        System.out.println("------- Calling query command with showFactHandleMaps: " + showFactHandleMaps +
                                   " showResultMaps: " + showResultMaps + "" +
                                   "arguments: " );
        if(arguments != null) {
            for (Object o : arguments) {
                System.out.println(o);
                if (o instanceof Iterable) {
                    System.out.println("Collection elements:");
                    Iterable list = (Iterable) o;
                    for (Object io : list) {
                        System.out.println(io);
                    }
                }
            }
        }

        if ( this.arguments == null || this.arguments.isEmpty() ) {
            this.arguments = Collections.emptyList();
        }

        for (int j = 0; j < arguments.size(); j++) {
            if (arguments.get(j) instanceof Variable) {
                arguments.set(j, Variable.v);
            }
        }

        QueryResults results = ksession.getQueryResults( name, this.arguments.toArray() );

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult( this.outIdentifier,
                                                                                       new FlatQueryResults( showFactHandleMaps,
                                                                                                             showResultMaps,
                                                                                                             (QueryResultsImpl) results) );
        }

        return results;
    }

    public String toString() {
        return "QueryCommand{" +
                "outIdentifier='" + outIdentifier + '\'' +
                ", name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
