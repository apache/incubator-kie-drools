/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.factmodel.traits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;


public class NullTraitType implements TraitType, Thing, Externalizable {

    private BitSet typeCode;

    public NullTraitType() {
    }

    public NullTraitType( BitSet code ) {
        typeCode = code;
    }

    public BitSet _getTypeCode() {
        return typeCode;
    }

    public boolean _isVirtual() {
        return true;
    }

    public String _getTraitName() {
        return "";
    }

    @Override
    public boolean _hasTypeCode( BitSet typeCode ) {
        return false;
    }

    public void _setTypeCode(BitSet typeCode) {
        this.typeCode = typeCode;
    }

    public Map<String, Object> getFields() {
        return Collections.EMPTY_MAP;
    }

    public Object getCore() {
        return null;
    }

    public boolean isTop() {
        return false;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject( typeCode );
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        typeCode = (BitSet) objectInput.readObject();
    }
}
