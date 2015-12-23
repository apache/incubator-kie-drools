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
 * This class defines a Rete "Entry Point".
 * An entry point is used as a source of facts that
 * are scoped to a separate alpha network. The alpha
 * network is not shared among separate entry points
 * and this allows them to safelly run in parallel
 * and concurrent modes.
 */
public class EntryPointId extends ConditionalElement
    implements
    PatternSource, Externalizable {

    public static final EntryPointId DEFAULT = new EntryPointId("DEFAULT");

    private static final long serialVersionUID = 510l;

    private String entryPointId;

    public EntryPointId() {

    }
    /**
     * Constructor.
     *
     * @param entryPointId the ID for this entry point
     */
    public EntryPointId( final String entryPointId ) {
        this.entryPointId = entryPointId;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        entryPointId    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(entryPointId);
    }

    /**
     * There is not reason to clone this object since it is stateless.
     * So a clone() call will return the instance itself.
     */
    @Override
    public EntryPointId clone() {
        return this;
    }

    /**
     * It is not possible to declare any new variables, so always
     * return an Empty Map
     */
    public Map<String, Declaration> getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * It is not possible to nest elements inside an entry point, so
     * always return an empty list.
     */
    public List<? extends RuleConditionElement> getNestedElements() {
        return Collections.EMPTY_LIST;
    }

    /**
     * It is not possible to declare and export any variables,
     * so always return an empty map
     */
    public Map<String, Declaration> getOuterDeclarations() {
        return Collections.EMPTY_MAP;
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
    public String getEntryPointId() {
        return this.entryPointId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((entryPointId == null) ? 0 : entryPointId.hashCode());
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
        final EntryPointId other = (EntryPointId) obj;
        if ( entryPointId == null ) {
            if ( other.entryPointId != null ) return false;
        } else if ( !entryPointId.equals( other.entryPointId ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "EntryPoint::"+this.entryPointId;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

    @Override
    public boolean requiresLeftActivation() {
        return false;
    }
}
