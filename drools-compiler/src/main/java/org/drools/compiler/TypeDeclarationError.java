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

package org.drools.compiler;

import org.drools.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;

public class TypeDeclarationError extends DroolsError {
    private String errorMessage;
    private int[]  line;
    private String namespace;

    public TypeDeclarationError(AbstractClassTypeDeclarationDescr typeDescr, String errorMessage) {
        super(typeDescr.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[] { typeDescr.getLine() };
        this.namespace = typeDescr.getNamespace();
    }

    public TypeDeclarationError(TypeFieldDescr fieldDescr, String errorMessage) {
        super(fieldDescr.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[] { fieldDescr.getLine() };
        this.namespace = fieldDescr.getNamespace();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public int[] getLines() {
        return this.line;
    }

    public String getMessage() {
        return this.errorMessage;
    }
    
    public String toString() {
        return this.getMessage();
    }

}
