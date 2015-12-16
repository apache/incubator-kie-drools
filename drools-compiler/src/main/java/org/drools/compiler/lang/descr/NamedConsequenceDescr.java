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

package org.drools.compiler.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class NamedConsequenceDescr extends BaseDescr {

    private boolean breaking;

    public NamedConsequenceDescr() { }

    public NamedConsequenceDescr( String id ) {
        this.setText( id );
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        breaking = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeBoolean( breaking );
    }

    public String getName() {
        return getText();
    }

    public void setName( String name) {
        setText( name );
    }

    public boolean isBreaking() {
        return breaking;
    }

    public void setBreaking(boolean breaking) {
        this.breaking = breaking;
    }

    @Override
    public String toString() {
        return (isBreaking() ? " break" : "do") + "[" + getName() + "]";
    }
}
