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

package org.drools.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.definition.rule.Rule;

public class SerializedRule
    implements
    Rule,
    Externalizable {
    private String name;
    private String packageName;
    private Map<String, Object> metaAttributes;
    
    public SerializedRule() {
        
    }
    
    public SerializedRule(Rule rule) {
        this.name = rule.getName();
        this.packageName = rule.getPackageName();
        this.metaAttributes = new HashMap<String, Object>( rule.getMetaData() );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( name );
        out.writeUTF( packageName );
        out.writeObject( this.metaAttributes );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        name = in.readUTF();
        packageName = in.readUTF();
        this.metaAttributes = ( Map<String, Object> ) in.readObject();
    }

    public String getName() {
        return this.name;
    }

    public String getPackageName() {
        return this.packageName;
    }
    
    @Deprecated
    public String getMetaAttribute(String identifier) {
        return this.metaAttributes.get( identifier ).toString();
    }

    @Deprecated
    public Collection<String> listMetaAttributes() {
        return this.metaAttributes.keySet();
    }

    @Deprecated
    public Map<String, Object> getMetaAttributes() {
        return Collections.unmodifiableMap( this.metaAttributes );
    }    

    public Map<String, Object> getMetaData() {
        return Collections.unmodifiableMap( this.metaAttributes );
    }    

}
