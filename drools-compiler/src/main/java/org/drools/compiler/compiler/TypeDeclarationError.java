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

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.core.rule.TypeDeclaration;

public class TypeDeclarationError extends DroolsError {
    private String errorMessage;
    private int[]  line;
    private String namespace;

    public TypeDeclarationError(BaseDescr typeDescr, String errorMessage) {
        super(typeDescr.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[] { typeDescr.getLine() };
        this.namespace = typeDescr.getNamespace();
    }

    public TypeDeclarationError(TypeDeclaration typeDeclaration, String errorMessage) {
        super(typeDeclaration.getResource());
        this.errorMessage = errorMessage;
        this.line = new int[0];
        this.namespace = typeDeclaration.getNamespace();
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
