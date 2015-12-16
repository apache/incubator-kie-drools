/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class defines a reference to a declared window
 */
public class WindowReference extends ConditionalElement
    implements
    PatternSource, Externalizable {

    private static final long serialVersionUID = 540l;

    private String name;

    public WindowReference() {
    }
    
    public WindowReference( final String name ) {
        this.name = name;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
    }

    /**
     * There is not reason to clone this object since it is stateless.
     * So a clone() call will return the instance itself.
     */
    @Override
    public WindowReference clone() {
        return this;
    }

    /**
     * It is not possible to declare any new variables, so always
     * return an Empty Map
     */
    public Map<String,Declaration> getInnerDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * It is not possible to nest elements inside an entry point, so
     * always return an empty list.
     */
    public List<RuleConditionElement> getNestedElements() {
        return Collections.emptyList();
    }

    /**
     * It is not possible to declare and export any variables,
     * so always return an empty map
     */
    public Map<String,Declaration> getOuterDeclarations() {
        return Collections.emptyMap();
    }

    /**
     * Not possible to resolve any declaration, so always return null.
     */
    public Declaration resolveDeclaration(String identifier) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final WindowReference other = (WindowReference) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Window::"+this.name;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }
}
