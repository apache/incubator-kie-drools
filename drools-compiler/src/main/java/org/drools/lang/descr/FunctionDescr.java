package org.drools.lang.descr;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionDescr extends BaseDescr {
    private static final long serialVersionUID = 320;
    
    private final String name;
    private final String returnType;
    
    private List         parameterTypes = Collections.EMPTY_LIST;
    private List         parameterNames = Collections.EMPTY_LIST;

    private String       text;
    
    private int      offset;

    private String   className;

    public FunctionDescr(final String name,
                         final String returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public String getName() {
        return this.name;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public List getParameterNames() {
        return this.parameterNames;
    }

    public List getParameterTypes() {
        return this.parameterTypes;
    }

    public void addParameter(final String type,
                             final String name) {
        if ( this.parameterTypes == Collections.EMPTY_LIST ) {
            this.parameterTypes = new ArrayList();
        }
        this.parameterTypes.add( type );

        if ( this.parameterNames == Collections.EMPTY_LIST ) {
            this.parameterNames = new ArrayList();
        }
        this.parameterNames.add( name );
    }

    public String getReturnType() {
        return this.returnType;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    
    
}