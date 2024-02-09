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
package org.drools.verifier.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.misc.Multimap;

public class ObjectType extends VerifierComponent<BaseDescr>
    implements
    Serializable {
    private static final long   serialVersionUID = 510l;

    private int                 offset           = 0;

    private String              fullName;

    private String              name;

    private Set<Field>          fields           = new HashSet<>();


    private Multimap<String, String> metadata         = new Multimap<>();

    
    public ObjectType(BaseDescr descr) {
        super(descr);
    }
    
    public int getOffset() {
        offset++;
        return offset % 2;
    }

    @Override
    public String getPath() {
        return String.format( "objectType[@name='%s']",
                              getName() );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.OBJECT_TYPE;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String toString() {
        return "ObjectType: " + fullName;
    }

    public Multimap<String, String> getMetadata() {
        return metadata;
    }
}
