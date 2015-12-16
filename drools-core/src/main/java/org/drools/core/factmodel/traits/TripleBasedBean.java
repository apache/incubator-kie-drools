/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.factmodel.traits;

import org.drools.core.util.Triple;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class TripleBasedBean extends TripleBasedStruct {

    protected Object object;

    public TripleBasedBean() {
    }

    public TripleBasedBean( Object o, TripleStore store, TripleFactory factory ) {
        super();
        this.store = store;
        this.storeId = store.getId();
        this.object = o;
        this.tripleFactory  = factory;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "TripleBasedBean{" +
                "object=" + object +
                '}';
    }
}
