/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.facttemplates;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.base.definitions.InternalKnowledgePackage;


public class FactTemplateImpl implements FactTemplate {

    private InternalKnowledgePackage pkg;
    private String name;
    private SortedMap<String, FieldTemplate> fields = new TreeMap<>();

    public FactTemplateImpl() {
        
    }

    public FactTemplateImpl(final InternalKnowledgePackage pkg,
                            final String name,
                            final FieldTemplate... fields) {
        this.pkg = pkg;
        this.name = name;
        this.pkg.addFactTemplate( this );

        if (fields != null && fields.length > 0) {
            this.fields = new TreeMap<>();
            for (FieldTemplate field : fields) {
                this.fields.put(field.getName(), field);
            }
        } else {
            this.fields = Collections.emptySortedMap();
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pkg     = (InternalKnowledgePackage)in.readObject();
        name    = (String)in.readObject();
        fields  = (SortedMap<String, FieldTemplate>)in.readObject();
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
        return this.fields.size();
    }

    /**
     * Return all the slots
     * @return
     */
    public Collection<String> getFieldNames() {
        return this.fields.keySet();
    }

    /**
     * A convienance method for finding the slot matching
     * the String name.
     * @param name
     * @return
     */
    public FieldTemplate getFieldTemplate(final String name) {
        return this.fields.get(name);
    }

    /**
     * Look up the pattern index of the slot
     */
    public int getFieldTemplateIndex(final String name) {
        int i = 0;
        for ( String field : fields.keySet() ) {
            if ( field.equals( name ) ) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Method takes a list of Slots and creates a deffact from it.
     */
    public Fact createFact() {
        return new FactImpl( this );
    }

    public String toString() {
        return "FactTemplate of: " + this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FactTemplateImpl that = (FactTemplateImpl) o;
        return pkg.getName().equals(that.pkg.getName()) && name.equals(that.name) && fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg.getName(), name, fields);
    }
}
