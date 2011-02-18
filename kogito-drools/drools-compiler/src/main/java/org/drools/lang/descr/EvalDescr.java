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

import java.util.Collections;
import java.util.List;

public class EvalDescr extends BaseDescr
    implements
    ConditionalElementDescr {
    private static final long serialVersionUID = 510l;

    private Object            content;

    private String[]          declarations;

    private String            classMethodName;

    public EvalDescr() {
    }

    public EvalDescr(final Object content) {
        this.content = content;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(final Object content) {
        this.content = content;
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(final String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public void setDeclarations(final String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public List getDescrs() {
        return Collections.EMPTY_LIST;
    }

    public void addDescr(final BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }
    
    public void insertBeforeLast(final Class clazz ,final BaseDescr baseDescr ) {
        throw new UnsupportedOperationException( "Can't add descriptors to " + this.getClass().getName() );
    }

    public void addOrMerge(BaseDescr baseDescr) {
        throw new UnsupportedOperationException("Can't add descriptors to "+this.getClass().getName());
    }

}
