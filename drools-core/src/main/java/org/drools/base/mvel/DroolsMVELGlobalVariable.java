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

package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.mvel2.integration.VariableResolver;

public class DroolsMVELGlobalVariable
    implements
    VariableResolver,
    Externalizable {

    private static final long serialVersionUID = -2480015657934353449L;
    
    private String            name;
    private Class             knownType;
    private DroolsGlobalVariableMVELFactory factory;

    public DroolsMVELGlobalVariable() {
    }

    public DroolsMVELGlobalVariable(final String identifier,
                                    final Class knownType,
                                    final DroolsGlobalVariableMVELFactory factory) {
        this.name = identifier;
        this.factory = factory;
        this.knownType = knownType;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        knownType   = (Class)in.readObject();
        factory     = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(knownType);
        out.writeObject(factory);
    }

    public String getName() {
        return this.name;
    }

    public Class getKnownType() {
        return this.knownType;
    }

    public Object getValue() {
        return this.factory.getValue( this.name );
    }

    public void setValue(final Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }

    public int getFlags() {
        return 0;
    }

    /**
     * Not used in drools.
     */
    public Class getType() {
        return this.knownType;
    }

    /**
     * Not used in drools.
     */
    public void setStaticType(Class arg0) {
    }

}
