package org.drools.base;
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

import java.util.List;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper {
    private final Rule          rule;
    private final Tuple         tuple;
    private final WorkingMemory workingMemory;

    public DefaultKnowledgeHelper(Rule rule,
                                  Tuple tuple,
                                  WorkingMemory workingMemory) {
        this.rule = rule;
        this.tuple = tuple;
        this.workingMemory = workingMemory;
    }

    public void assertObject(Object object) throws FactException {
        this.workingMemory.assertObject( object );
    }

    public void assertObject(Object object,
                             boolean dynamic) throws FactException {
        this.workingMemory.assertObject( object,
                                         dynamic );
    }

    public void modifyObject(Object object) throws FactException {
        FactHandle handle = this.workingMemory.getFactHandle( object );

        this.workingMemory.modifyObject( handle,
                                         object );
    }

    public void modifyObject(FactHandle handle,
                             Object newObject) throws FactException {
        this.workingMemory.modifyObject( handle,
                                         newObject );
    }

    public void retractObject(Object object) throws FactException {
        retractObject( this.workingMemory.getFactHandle( object ) );
    }

    public void retractObject(FactHandle handle) throws FactException {
        this.workingMemory.retractObject( handle );
    }

    public Rule getRule() {
        return this.rule;
    }

    public List getObjects() {
        return this.workingMemory.getObjects();
    }

    public List getObjects(Class objectClass) {
        return this.workingMemory.getObjects( objectClass );
    }

    public void clearAgenda() {
        this.workingMemory.clearAgenda();
    }

    public Object get(Declaration declaration) {
        return declaration.getValue( this.workingMemory.getObject( this.tuple.get( declaration ) ) );
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
}
