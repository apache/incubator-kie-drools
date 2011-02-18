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

package org.drools.facttemplates;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

//import woolfel.engine.rule.Rule;

public class FactImpl
    implements
    Fact,
    Externalizable {

    private static int hashCode(final Object[] array) {
        final int PRIME = 31;
        if ( array == null ) {
            return 0;
        }
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    private FactTemplate factTemplate = null;
    private Object[]     values       = null;
    private int          hashCode;

    /**
     * the Fact id must be unique, since we use it for the indexes
     */
    private long         id;

    public FactImpl() {
    }

    /**
     * this is the default constructor
     * @param instance
     * @param values
     */
    public FactImpl(final FactTemplate template,
                    final Object[] values,
                    final long id) {
        this.factTemplate = template;
        this.values = values;
        this.id = id;
    }

    public FactImpl(final FactTemplate template,
                    final long id) {
        this.factTemplate = template;
        this.values = new Object[template.getNumberOfFields()];
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factTemplate    = (FactTemplate)in.readObject();
        values          = (Object[])in.readObject();
        hashCode        = in.readInt();
        id              = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factTemplate);
        out.writeObject(values);
        out.writeInt(hashCode);
        out.writeLong(id);
    }

    /**
     * Method returns the value of the given slot at the
     * id.
     * @param id
     * @return
     */
    public Object getFieldValue(final int index) {
        return this.values[index];
    }

    public Object getFieldValue(final String name) {
        return this.values[this.factTemplate.getFieldTemplateIndex( name )];
    }

    public void setFieldValue(final String name,
                              final Object value) {
        setFieldValue( this.factTemplate.getFieldTemplateIndex( name ),
                       value );
    }

    public void setFieldValue(final int index,
                              final Object value) {
        this.values[index] = value;
    }

    /**
     * Return the long factId
     */
    public long getFactId() {
        return this.id;
    }

    /**
     * this is used to reset the id, in the event an user tries to
     * assert the same fact again, we reset the id to the existing one.
     * @param fact
     */
    protected void resetId(final Fact fact) {
        this.id = fact.getFactId();
    }

    /**
     * Return the deftemplate for the fact
     */
    public FactTemplate getFactTemplate() {
        return this.factTemplate;
    }

    public int hashCode() {
        if ( this.hashCode == 0 ) {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + this.factTemplate.hashCode();
            result = PRIME * result + FactImpl.hashCode( this.values );
            this.hashCode = result;

        }
        return this.hashCode;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || FactImpl.class != object.getClass() ) {
            return false;
        }

        final FactImpl other = (FactImpl) object;

        if ( !this.factTemplate.equals( other.factTemplate ) ) {
            return false;
        }

        if ( !Arrays.equals( this.values,
                             other.values ) ) {
            return false;
        }

        return true;
    }
}
