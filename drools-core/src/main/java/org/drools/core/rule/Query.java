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

package org.drools.core.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Query extends Rule implements org.kie.api.definition.rule.Query {

    private static final long serialVersionUID = 510l;

    public Query() {

    }
    
    private Declaration[] parameters;

    public Query(final String name) {
        super( name );
        setActivationListener( "query" );
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( parameters );
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.parameters = ( Declaration[] ) in.readObject();
    }

    /**
     * Override this as Queries will NEVER have a consequence, and it should
     * not be taken into account when deciding if it is valid.
     */
    public boolean isValid() {
        return super.isSemanticallyValid();
    }

    public void setParameters(Declaration[] parameters) {
        this.parameters = parameters;
    }

    public Declaration[] getParameters() {
        return this.parameters;
    }
    
    @Override
    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.QUERY;
    }

    public boolean isQuery() {
        return true;
    }

    public boolean isAbductive() {
        return false;
    }

}
