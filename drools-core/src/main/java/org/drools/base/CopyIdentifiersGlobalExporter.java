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

/**
 *
 */
package org.drools.base;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalRuleBase;
import org.drools.WorkingMemory;
import org.drools.spi.GlobalExporter;
import org.drools.spi.GlobalResolver;

/**
 * Creates a new GlobalResolver consisting of just the identifiers specified in the String[].
 * If the String[] is null, or the default constructor is used, then all globals defined in the RuleBase
 * will be copied.
 *
 */
public class CopyIdentifiersGlobalExporter implements GlobalExporter {
    private String[] identifiers;

    /**
     * All identifiers will be copied
     *
     */
    public CopyIdentifiersGlobalExporter() {
        this.identifiers = null;
    }

    /**
     * Specified identifiers will be copied
     * @param identifiers
     */
    public CopyIdentifiersGlobalExporter(String[] identifiers) {
        this.identifiers = identifiers;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        identifiers = (String[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(identifiers);
    }

    public GlobalResolver export(WorkingMemory workingMemory) {
        if ( this.identifiers == null || this.identifiers.length == 0 ) {
            // no identifiers, to get all the identifiers from that defined in
            // the rulebase
            Map map = ((InternalRuleBase)workingMemory.getRuleBase()).getGlobals();
            this.identifiers = new String[ map.size() ];
            this.identifiers = (String[]) map.keySet().toArray( this.identifiers );
        }

        Map map = new HashMap(identifiers.length);
        for ( int i = 0, length = identifiers.length; i < length; i++ ) {
            map.put( identifiers[i], workingMemory.getGlobal( identifiers[i] ) );
        }
        return new MapGlobalResolver(map);
    }
}