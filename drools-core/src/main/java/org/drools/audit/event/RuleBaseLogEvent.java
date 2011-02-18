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
 * A rulebase event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the rulebase.
 */
public class RuleBaseLogEvent extends LogEvent {

    private String packageName;
    private String ruleName;

    /**
     * Create a new activation log event.
     * 
     * @param type The type of event.  
     * @param packageName The name of the package
     * @param ruleName The name of the rule 
     */
    public RuleBaseLogEvent(final int type,
                            final String packageName,
                            final String ruleName) {
        super( type );
        this.packageName = packageName;
        this.ruleName = ruleName;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        packageName    = (String)in.readObject();
        ruleName    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(packageName);
        out.writeObject(ruleName);
    }

    /**
     * Returns the Package Name
     * 
     * @return The name of the package
     */
    public String getPackageName() {
        return this.packageName;
    }

    /**
     * Returns the name of the rule 
     * 
     * @return The name of the rule
     */
    public String getRuleName() {
        return this.ruleName;
    }

    public String toString() {

        String msg = null;
        switch ( this.getType() ) {
            case BEFORE_PACKAGE_ADDED :
                msg = "BEFORE PACKAGE ADDED";
                break;
            case AFTER_PACKAGE_ADDED :
                msg = "AFTER PACKAGE ADDED";
                break;
            case BEFORE_PACKAGE_REMOVED :
                msg = "BEFORE PACKAGE REMOVED";
                break;
            case AFTER_PACKAGE_REMOVED :
                msg = "AFTER PACKAGE REMOVED";
                break;
            case BEFORE_RULE_ADDED :
                msg = "BEFORE RULE ADDED";
                break;
            case AFTER_RULE_ADDED :
                msg = "AFTER RULE ADDED";
                break;
            case BEFORE_RULE_REMOVED :
                msg = "BEFORE RULE REMOVED";
                break;
            case AFTER_RULE_REMOVED :
                msg = "AFTER RULE REMOVED";
                break;
        }
        String ruleMsg = "";
        if( this.ruleName != null ) {
            ruleMsg = " rule: \""+this.ruleName+"\"";
        }
        return msg + " package: \"" + this.packageName + "\""+ruleMsg;
    }
}
