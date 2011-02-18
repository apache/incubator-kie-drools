/*
 * Copyright 2005 JBoss Inc
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

package org.drools.audit.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An activation event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the activation id, the name of the rule and a String
 * representing the declarations of the activation, which is a list of
 * name-value-pairs for each of the declarations in the tuple of the
 * activation.  The name is the identifier (=name) of the
 * declaration, and the value is a toString of the value of the
 * parameter, followed by the id of the fact between parentheses.
 * e.g. param1=10; param2=Person[John Doe]
 * 
 * Such a String representation is used to create a snapshot of the
 * current state of the activation by storing a toString of the facts
 * bound in the activation.  If necessary, this event could be extended
 * to contain a map of declarations too.
 * 
 */
public class ActivationLogEvent extends LogEvent {

    private String activationId;
    private String rule;
    private String declarations;
    private String ruleFlowGroup;

    public ActivationLogEvent() {
    }

    /**
     * Create a new activation log event.
     * 
     * @param type The type of event.  This can only be ACTIVATION_CREATED, ACTIVATION_CANCELLED,
     * BEFORE_ACTIVATION_FIRE or AFTER_ACTIVATION_FIRE.
     * @param activationId The id of the activation
     * @param rule The name of the rule of the activation
     * @param declarations A String representation of the declarations in the
     * activation.
     */
    public ActivationLogEvent(final int type,
                              final String activationId,
                              final String rule,
                              final String declarations,
                              final String ruleFlowGroup) {
        super( type );
        this.activationId = activationId;
        this.rule = rule;
        this.declarations = declarations;
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        activationId    = (String)in.readObject();
        rule    = (String)in.readObject();
        declarations    = (String)in.readObject();
        ruleFlowGroup    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(activationId);
        out.writeObject(rule);
        out.writeObject(declarations);
        out.writeObject(ruleFlowGroup);
    }

    /**
     * Returns a unique id for the activation.
     * 
     * @return The id of the activation
     */
    public String getActivationId() {
        return this.activationId;
    }

    /**
     * Returns the name of the rule of the activation.
     * 
     * @return The name of the rule
     */
    public String getRule() {
        return this.rule;
    }

    /**
     * Returns a String representation of the declarations in the
     * activation.
     * 
     * @return A String representation of the declarations.
     */
    public String getDeclarations() {
        return this.declarations;
    }
    
    public String getRuleFlowGroup() {
        return ruleFlowGroup;
    }

    public String toString() {

        String msg = null;
        switch ( this.getType() ) {
            case ACTIVATION_CANCELLED :
                msg = "ACTIVATION CANCELLED";
                break;
            case ACTIVATION_CREATED :
                msg = "ACTIVATION CREATED";
                break;

            case AFTER_ACTIVATION_FIRE :
                msg = "AFTER ACTIVATION FIRED";
                break;
            case BEFORE_ACTIVATION_FIRE :
                msg = "BEFORE ACTIVATION FIRED";
                break;
        }
        return msg + " rule:" + this.rule + " activationId:" + this.activationId + " declarations: " + this.declarations + (ruleFlowGroup == null ? "" : " ruleflow-group: " + ruleFlowGroup);
    }
}
