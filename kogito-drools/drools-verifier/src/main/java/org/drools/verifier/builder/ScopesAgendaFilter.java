/**
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

package org.drools.verifier.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

/**
 * 
 * @author rikkola
 *
 */
public class ScopesAgendaFilter
    implements
    AgendaFilter {

    public final static String             VERIFYING_SCOPE_SINGLE_RULE       = "single-rule";
    public final static String             VERIFYING_SCOPE_DECISION_TABLE    = "decision-table";
    public final static String             VERIFYING_SCOPE_KNOWLEDGE_PACKAGE = "knowledge-package";

    public final static Collection<String> ALL_SCOPES                        = new ArrayList<String>() {

                                                                                 private static final long serialVersionUID = 4428467530400926541L;

                                                                                 {
                                                                                     add( VERIFYING_SCOPE_DECISION_TABLE );
                                                                                     add( VERIFYING_SCOPE_SINGLE_RULE );
                                                                                     add( VERIFYING_SCOPE_KNOWLEDGE_PACKAGE );
                                                                                 }
                                                                             };

    public static final String             VERIFYING_SCOPES                  = "verifying_scopes";

    private final boolean                  acceptEmpty;

    private final Collection<String>       scopes;

    /**
     * 
     * @param acceptEmpty true accepts rules that do not have scope set.
     * @param scopes Valid scope values.
     */
    public ScopesAgendaFilter(boolean acceptEmpty,
                              Collection<String> scopes) {
        this.acceptEmpty = acceptEmpty;
        this.scopes = scopes;
    }

    public ScopesAgendaFilter(boolean acceptEmpty,
                              String scope) {
        this.acceptEmpty = acceptEmpty;

        Collection<String> list = new ArrayList<String>();
        list.add( scope );
        this.scopes = list;
    }

    public boolean accept(Activation activation) {
        if ( acceptEmpty && activation.getRule().listMetaAttributes().isEmpty() ) {
            return true;
        }

        if ( activation.getRule().listMetaAttributes().contains( VERIFYING_SCOPES ) ) {
            String[] values = activation.getRule().getMetaAttribute( VERIFYING_SCOPES ).split( "," );

            for ( String value : values ) {
                if ( scopes.contains( value.trim() ) ) {
                    return true;
                }
            }
        }

        return false;
    }

}
