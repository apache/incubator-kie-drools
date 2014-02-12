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

package org.drools.core.facttemplates;

import org.drools.core.definitions.InternalKnowledgePackage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;


public class FactTemplateImpl
    implements
    FactTemplate {
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

    private FieldTemplate[] fields;
    private InternalKnowledgePackage pkg;
    private String          name;

    public FactTemplateImpl() {
        
    }

    public FactTemplateImpl(final InternalKnowledgePackage pkg,
                            final String name,
                            final FieldTemplate[] fields) {
        this.pkg = pkg;
        this.name = name;
        this.fields = fields;
        this.pkg.addFactTemplate( this );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pkg     = (InternalKnowledgePackage)in.readObject();
        name    = (String)in.readObject();
        fields  = (FieldTemplate[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(pkg);
        out.writeObject(name);
        out.writeObject(fields);
    }

    public InternalKnowledgePackage getPackage() {
        return this.pkg;
    }

    /**
     * the template name is an alias for an object
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the number of slots in the deftemplate
     * @return
     */
    public int getNumberOfFields() {
        return this.fields.length;
    }

    /**
     * Return all the slots
     * @return
     */
    public FieldTemplate[] getAllFieldTemplates() {
        return this.fields;
    }

    /**
     * A convienance method for finding the slot matching
     * the String name.
     * @param name
     * @return
     */
    public FieldTemplate getFieldTemplate(final String name) {
        for ( int idx = 0; idx < this.fields.length; idx++ ) {
            if ( this.fields[idx].getName().equals( name ) ) {
                return this.fields[idx];
            }
        }
        return null;
    }

    /**
     * get the Slot at the given pattern id
     */
    public FieldTemplate getFieldTemplate(final int index) {
        return this.fields[index];
    }

    /**
     * Look up the pattern index of the slot
     */
    public int getFieldTemplateIndex(final String name) {
        for ( int index = 0; index < this.fields.length; index++ ) {
            if ( this.fields[index].getName().equals( name ) ) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Method takes a list of Slots and creates a deffact from it.
     */
    public Fact createFact(final long id) {
        return new FactImpl( this,
                             id );
    }

    /**
     * Method will return a string format with the int type code
     * for the slot type
     */
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append( "(" + this.name + " " );
        //        for (int idx=0; idx < this.slots.length; idx++){
        //            buf.append("(" + this.slots[idx].getName() +
        //                    " (type " + ConversionUtils.getTypeName(
        //                            this.slots[idx].getValueType()) +
        //                    ") ) ");
        //        }
        //        if (this.clazz != null){
        //            buf.append("[" + this.clazz.getClassObject().getName() + "] ");
        //        }
        buf.append( ")" );
        return buf.toString();
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + FactTemplateImpl.hashCode( this.fields );
        result = PRIME * result + this.name.hashCode();
        result = PRIME * result + this.pkg.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final FactTemplateImpl other = (FactTemplateImpl) object;
        if ( !Arrays.equals( this.fields,
                             other.fields ) ) {
            return false;
        }

        return this.pkg.equals( other.pkg ) && this.name.equals( other.name );
    }

}
