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

package org.drools.verifier.report.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Toni Rikkola
 */
abstract public class VerifierMessageBase
    implements
    Serializable,
    Comparable<VerifierMessageBase> {
    private static final long   serialVersionUID = 510l;

    private static int          index            = 0;

    // <path,rule name>
    private Map<String, String> impactedRules    = new HashMap<String, String>();

    protected final Severity    severity;
    protected final MessageType messageType;

    protected final int         id               = index++;
    protected final Cause       faulty;
    protected final String      message;

    public int compareTo(VerifierMessageBase o) {
        if ( id == o.getId() ) {
            return 0;
        }

        return (id > o.getId() ? 1 : -1);
    }

    protected VerifierMessageBase(Map<String, String> impactedRules,
                                  Severity severity,
                                  MessageType messageType,
                                  Cause faulty,
                                  String message) {
        this.impactedRules = impactedRules;
        this.severity = severity;
        this.messageType = messageType;
        this.faulty = faulty;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Cause getFaulty() {
        return faulty;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer( severity.singular );

        str.append( " id = " );
        str.append( id );
        str.append( ":\n" );

        if ( faulty != null ) {
            str.append( "faulty : " );
            str.append( faulty );
            str.append( ", " );
        }

        str.append( message );

        str.append( "\t]" );

        return str.toString();
    }

    public void setImpactedRules(Map<String, String> impactedRules) {
        this.impactedRules = impactedRules;
    }

    public Map<String, String> getImpactedRules() {
        return impactedRules;
    }

    public abstract Collection<Cause> getCauses();
}
