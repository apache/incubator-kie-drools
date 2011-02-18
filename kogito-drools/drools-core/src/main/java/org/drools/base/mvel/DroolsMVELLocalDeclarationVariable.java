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

package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.mvel2.integration.VariableResolver;

public class DroolsMVELLocalDeclarationVariable
    implements
    VariableResolver,
    Externalizable  {

    private static final long serialVersionUID = 510l;
    
    private Declaration       declaration;
    private DroolsMVELFactory factory;

    public DroolsMVELLocalDeclarationVariable() {
    }

    public DroolsMVELLocalDeclarationVariable(final Declaration declaration,
                                              final DroolsMVELFactory factory) {
        this.declaration = declaration;
        this.factory = factory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        declaration = (Declaration)in.readObject();
        factory     = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(declaration);
        out.writeObject(factory);
    }

    public String getName() {
        return this.declaration.getIdentifier();
    }

    public Class getKnownType() {
        return this.declaration.getExtractor().getExtractToClass();
    }

    public Object getValue() {
        return this.declaration.getValue( (InternalWorkingMemory) this.factory.getWorkingMemory(), this.factory.getObject() );
    }

    public void setValue(final Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }

    public int getFlags() {
        return 0;
    }

    public Class getType() {
        return this.declaration.getExtractor().getExtractToClass();
    }
    
    /**
     * Not used in drools.
     */
    public void setStaticType(Class arg0) {
    }

}
