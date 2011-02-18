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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.spi.DataProvider;
import org.drools.spi.Wireable;

public class From extends ConditionalElement
    implements
    Externalizable,
    Wireable,
    PatternSource {

    private static final long serialVersionUID = 510l;

    private DataProvider      dataProvider;      

    public From() {
    }

    public From(final DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        dataProvider    = (DataProvider)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(dataProvider);
    }
    
    public void wire(Object object) {
        this.dataProvider = ( DataProvider ) object;
    }    

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public Object clone() {
        return new From( this.dataProvider.clone() );
    }

    public Map getInnerDeclarations() {
        return Collections.EMPTY_MAP;
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return null;
    }

    public List getNestedElements() {
        return Collections.EMPTY_LIST;
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

}
