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

import org.drools.spi.KnowledgeHelper;
import org.mvel2.integration.VariableResolver;

public class DroolsMVELKnowledgeHelper
    implements
    VariableResolver,
    Externalizable  {

    private static final long serialVersionUID = 9175428283083361478L;
    
    private DroolsMVELFactory factory;
    public static final String DROOLS = "drools";
    public static final String CONTEXT = "kcontext";

    public DroolsMVELKnowledgeHelper() {
    }

    public DroolsMVELKnowledgeHelper(final DroolsMVELFactory factory) {
        this.factory = factory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factory = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factory);
    }

    public String getName() {
        return DROOLS;
    }

    public Class getKnownType() {
        return KnowledgeHelper.class;
    }

    public Object getValue() {
        return this.factory.getKnowledgeHelper();
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
        return KnowledgeHelper.class;
    }

    /**
     * Not used in drools.
     */
    public void setStaticType(Class arg0) {
    }

}
