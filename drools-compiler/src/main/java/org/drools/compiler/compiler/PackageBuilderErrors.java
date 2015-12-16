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

package org.drools.compiler.compiler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;

public class PackageBuilderErrors extends ArrayList<KnowledgeBuilderError>
    implements
        KnowledgeBuilderErrors,
    Externalizable {
    private DroolsError[] errors;

    public PackageBuilderErrors() {
        super();
    }

    public PackageBuilderErrors(DroolsError[] errors) {
        super( errors.length );
        this.errors = errors;

        for ( DroolsError error : errors ) {
            add( error );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        SerializableDroolsError[] temp = (SerializableDroolsError[]) in.readObject();
        this.errors = temp;
        for ( DroolsError error : temp ) {
            add( error );
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( !this.errors.getClass().getComponentType().equals( SerializableDroolsError.class ) ) {
            SerializableDroolsError[] temp = new SerializableDroolsError[this.errors.length];
            int i = 0;
            for ( DroolsError error : this.errors ) {
                temp[i] = new SerializableDroolsError( error );
            }
            out.writeObject( temp );
        } else {
            out.writeObject( this.errors );
        }
    }

    public DroolsError[] getErrors() {
        return errors;
    }

    public boolean isEmpty() {
        return this.errors.length == 0;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        for ( int i = 0, length = this.errors.length; i < length; i++ ) {
            buf.append( errors[i] );
            buf.append( "\n" );
        }
        return buf.toString();
    }
}
