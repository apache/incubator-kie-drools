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
package org.drools.compiler.compiler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.drl.parser.BaseKnowledgeBuilderResultImpl;
import org.drools.drl.parser.DroolsError;

public class SerializableDroolsError extends DroolsError implements Externalizable {
    private int[] errorLines;
    private String errorClassName;
    private String namespace;

    private String originalMessage;

    public SerializableDroolsError() {
        super("");
        this.originalMessage = getMessage();
    }
    
    public SerializableDroolsError(BaseKnowledgeBuilderResultImpl error) {
        super(error.getMessage());
        this.originalMessage = getMessage();
        this.errorLines = error.getLines();
        this.errorClassName = error.getClass().getName();
        this.namespace = error instanceof DroolsError ? ((DroolsError)error).getNamespace() : "";
    }

    @Override
    public String getNamespace() {
        return namespace;
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
        out.writeObject(this.originalMessage);
        out.writeObject( this.errorLines );
        out.writeObject( this.errorClassName );
        out.writeObject( this.namespace );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.originalMessage = ( String ) in.readObject();
        this.errorLines = ( int[] ) in.readObject();
        this.errorClassName = ( String ) in.readObject();
        this.namespace = ( String ) in.readObject();
    }
    
}
