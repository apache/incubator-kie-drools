/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SerializableDroolsError extends DroolsError implements Externalizable {
    private String message;
    private int[] errorLines;
    private String errorClassName;
    private String namespace;

    public SerializableDroolsError() { }
    
    public SerializableDroolsError(BaseKnowledgeBuilderResultImpl error) {
        this.message = error.getMessage();
        this.errorLines = error.getLines();
        this.errorClassName = error.getClass().getName();
        this.namespace = error instanceof DroolsError ? ((DroolsError)error).getNamespace() : "";
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     * Classes that extend this must provide a printable message,
     * which summarises the error.
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Returns the lines of the error in the source file
     * @return
     */
    public int[] getLines() {
        return this.errorLines;
    }
    
    public String toString() {
        return this.errorClassName + ": " + getMessage();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.message );
        out.writeObject( this.errorLines );
        out.writeObject( this.errorClassName );
        out.writeObject( this.namespace );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.message = ( String ) in.readObject();
        this.errorLines = ( int[] ) in.readObject();
        this.errorClassName = ( String ) in.readObject();
        this.namespace = ( String ) in.readObject();
    }
    
}
