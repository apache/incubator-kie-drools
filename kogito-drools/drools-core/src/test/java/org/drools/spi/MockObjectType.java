/**
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

package org.drools.spi;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.ValueType;

/**
 * Java class semantics <code>ObjectType</code>.
 *
 * @author <a href="mailto:bob@werken.com">bob@werken.com </a>
 *
 * @version $Id: MockObjectType.java,v 1.1 2005/07/26 01:06:34 mproctor Exp $
 */
public class MockObjectType
    implements
    ObjectType {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     *
     */
    private static final long serialVersionUID = 510l;
    /** Java object class. */
    private boolean           matches;

    private boolean           isEvent;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public MockObjectType() {
        this(true);
    }
    /**
     * Construct.
     *
     * @param objectTypeClass
     *            Java object class.
     */
    public MockObjectType(final boolean matches) {
        this.matches = matches;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        matches = in.readBoolean();
        isEvent = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(matches);
        out.writeBoolean(isEvent);
    }
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.spi.ObjectType
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Determine if the passed <code>Object</code> belongs to the object type
     * defined by this <code>objectType</code> instance.
     *
     * @param object
     *            The <code>Object</code> to test.
     *
     * @return <code>true</code> if the <code>Object</code> matches this
     *         object type, else <code>false</code>.
     */
    public boolean matches(final Object object) {
        return this.matches;
    }

    public boolean isAssignableFrom(Object object) {
        return this.matches;
    }

    public boolean isAssignableFrom(ObjectType objectType) {
        return this.matches;
    }

    public ValueType getValueType() {
        return ValueType.OBJECT_TYPE;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }

}
