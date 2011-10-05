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

package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.io.Resource;
import org.drools.rule.Dialectable;
import org.drools.rule.Namespaceable;

public class FunctionDescr extends BaseDescr
    implements
    Dialectable,
    Namespaceable {
    private static final long serialVersionUID = 510l;

    private String            namespace;
    private String            name;
    private String            returnType;
    private String            dialect;

    private List<String>      parameterTypes   = Collections.emptyList();
    private List<String>      parameterNames   = Collections.emptyList();

    private String            className;
    
    // this seems to be used to map error line from the java generated file to the drl file
    private int               offset;

    private Resource          resource;

    public FunctionDescr() {
        this( null, null );
    }
    
    public FunctionDescr(final String name,
                         final String returnType) {
        this.name = name;
        this.returnType = returnType == null ? "void" : returnType;
        this.dialect = "java";
        this.namespace = null;
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource( Resource resource ) {
        this.resource = resource;
    }

    public String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setDialect( String dialect ) {
        this.dialect = dialect;
    }

    public String getDialect() {
        return this.dialect;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName( final String className ) {
        this.className = className;
    }

    public List<String> getParameterNames() {
        return this.parameterNames;
    }

    public List<String> getParameterTypes() {
        return this.parameterTypes;
    }

    public void addParameter( final String type,
                              final String name ) {
        if ( this.parameterTypes == Collections.EMPTY_LIST ) {
            this.parameterTypes = new ArrayList<String>();
        }
        this.parameterTypes.add( type );

        if ( this.parameterNames == Collections.EMPTY_LIST ) {
            this.parameterNames = new ArrayList<String>();
        }
        this.parameterNames.add( name );
    }

    public String getReturnType() {
        return this.returnType;
    }

    public void setReturnType( String type ) {
        this.returnType = type;
    }

    public void setBody( String body ) {
        setText( body );
    }

    public String getBody() {
        return getText();
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[FunctionDescr " + returnType + " " + name + "(" + parameterTypes + ") ]";
    }

}
