/*
 * Copyright 2008 Red Hat
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

package org.drools.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
 
public class EntryPointDeclarationDescr extends BaseDescr {

    private static final long            serialVersionUID = 530l;
    private String                       entryPoint = null;

    public EntryPointDeclarationDescr() {
        super();
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        this.entryPoint = (String) in.readObject();
    }
    
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( entryPoint );
    }

    public void setEntryPointId( String name ) {
        this.entryPoint = name;
    }

    public String getEntryPointId() {
        return this.entryPoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entryPoint == null) ? 0 : entryPoint.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        EntryPointDeclarationDescr other = (EntryPointDeclarationDescr) obj;
        if ( entryPoint == null ) {
            if ( other.entryPoint != null ) return false;
        } else if ( !entryPoint.equals( other.entryPoint ) ) return false;
        return true;
    }
}
