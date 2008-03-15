/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Nov 7, 2007
 */
package org.drools.rule;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

/**
 * A class to represent an import declaration. That declaration
 * may have additional metadata associated to it, like a flag
 * stating if the imported class is an event or not
 *
 * @author etirelli
 */
public class ImportDeclaration implements Externalizable {

    private static final long serialVersionUID = 6410032114027977766L;

    private String target;
    private boolean isEvent;

    /**
     * Creates an empty import declaration
     */
    public ImportDeclaration() {
        this( null, false );
    }

    /**
     * Creates an import declaration for the given target.
     *
     * @param target
     */
    public ImportDeclaration( String target ) {
        this( target, false );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        target  = (String)in.readObject();
        isEvent = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(target);
        out.writeBoolean(isEvent);
    }

    /**
     * Creates an import declaration for the given target.
     *
     * @param target the import target
     * @param isEvent true if the target is an event-type target, false otherwise.
     */
    public ImportDeclaration(String target,
                             boolean isEvent) {
        super();
        this.target = target;
        this.isEvent = isEvent;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean isEvent) {
        this.isEvent = isEvent;
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
        result = PRIME * result + (isEvent ? 1231 : 1237);
        result = PRIME * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ImportDeclaration other = (ImportDeclaration) obj;
        if ( isEvent != other.isEvent ) return false;
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
    public boolean matches( Class clazz ) {
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
