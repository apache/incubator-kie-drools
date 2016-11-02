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

public class ActionError extends DroolsError {
    private BaseDescr descr;
    private Object    object;
    private String    message;
    private int[]     errorLines = new int[0];

    public ActionError(final BaseDescr descr,
                     final Object object,
                     final String message) {
        this.descr = descr;
        this.object = object;
        this.message = message;
    }

    @Override
    public String getNamespace() {
        return descr.getNamespace();
    }

    public BaseDescr getDescr() {
        return this.descr;
    }

    public Object getObject() {
        return this.object;
    }

    public int[] getLines() {
        return this.errorLines;
    }

    /**
     * This will return the line number of the error, if possible
     * Otherwise it will be -1
     */
    public int getLine() {
        return this.descr != null ? this.descr.getLine() : -1;
    }

    public String getMessage() {
        return BuilderResultUtils.getProblemMessage( this.object, this.message, "\n" );
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append( this.message )
                .append( " : " )
                .append( "\n" );
        return BuilderResultUtils.appendProblems( this.object, builder ).toString();
    }

}
