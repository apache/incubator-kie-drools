package org.drools.spi;

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

public final class MockField
    implements
    FieldValue {

    /**
     * 
     */
    private static final long serialVersionUID = 6399579773906347733L;
    private final Object      value;

    public MockField(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean equals(final Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof MockField) ) {
            return false;
        }
        final MockField field = (MockField) other;

        return (((this.value == null) && (field.value == null)) || ((this.value != null) && (this.value.equals( field.value ))));
    }

    public int hashCode() {
        return this.value.hashCode();
    }

}