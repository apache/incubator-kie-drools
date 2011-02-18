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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A class to represent an import declaration. 
 */
public class ImportDeclaration implements Externalizable {

    private static final long serialVersionUID = 510l;

    private String target;

    /**
     * Creates an empty import declaration
     */
    public ImportDeclaration() {
        this( null );
    }

    /**
     * Creates an import declaration for the given target.
     *
     * @param target
     */
    public ImportDeclaration( String target ) {
        this.target = target;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        target  = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(target);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ImportDeclaration other = (ImportDeclaration) obj;
        if ( target == null ) {
            if ( other.target != null ) return false;
        } else if ( !target.equals( other.target ) ) return false;
        return true;
    }

    /**
     * Returns true if this ImportDeclaration correctly matches to
     * the given clazz
     *
     * @param name
     * @return
     */
    public boolean matches( Class<?> clazz ) {
        // fully qualified import?
        if( this.target.equals( clazz.getName() ) ) {
            return true;
        }

        // wild card imports
        if( this.target.endsWith( ".*" ) ) {
            String prefix = this.target.substring( 0, this.target.indexOf( ".*" ) );

            // package import: import my.package.*
            if( prefix.equals( clazz.getPackage().getName() ) ) {
                return true;
            }

            // inner class imports with wild card?
            // by looking at the ClassTypeResolver class, it seems we do not support
            // the usage of wild cards when importing static inner classes like the
            // java static imports allow
        }
        return false;
    }
}
