/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AccessorDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 510l;

    private String            variableName;
    private LinkedList        invokers;

    public AccessorDescr() {
        this( null );
    }

    public AccessorDescr(final String rootVariableName) {
        super();
        this.variableName = rootVariableName;
        this.invokers = new LinkedList();
    }

    public DeclarativeInvokerDescr[] getInvokersAsArray() {
        return (DeclarativeInvokerDescr[]) this.invokers.toArray( new DeclarativeInvokerDescr[0] );
    }

    public List getInvokers() {
        return this.invokers;
    }

    public void addInvoker(final DeclarativeInvokerDescr accessor) {
        this.invokers.add( accessor );
    }

    public void addFirstInvoker(final DeclarativeInvokerDescr accessor) {
        this.invokers.addFirst( accessor );
    }

    public String getVariableName() {
        return this.variableName;
    }

    public void setVariableName(final String methodName) {
        this.variableName = methodName;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append( (this.variableName != null) ? this.variableName : "" );
        for ( final Iterator it = this.invokers.iterator(); it.hasNext(); ) {
            if ( buf.length() > 0 ) {
                buf.append( "." );
            }
            buf.append( it.next().toString() );
        }
        return buf.toString();
    }

}
