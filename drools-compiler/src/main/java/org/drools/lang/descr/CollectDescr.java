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

/**
 * An AST class to describe "collect" conditional element
 * 
 * @author etirelli
 *
 */
public class CollectDescr extends PatternDescr
    implements
    ConditionalElementDescr {

    private static final long serialVersionUID = -78056848363435347L;
    
    private ColumnDescr       sourceColumn;
    private ColumnDescr       resultColumn;
    private String            classMethodName;

    CollectDescr() {
        super();
    }

    public int getLine() {
        return sourceColumn.getLine();
    }

    public void setSourceColumn(ColumnDescr sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public ColumnDescr getSourceColumn() {
        return sourceColumn;
    }

    public String getClassMethodName() {
        return classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public void setResultColumn(ColumnDescr resultColumn) {
        this.resultColumn = resultColumn;
    }
    
    public ColumnDescr getResultColumn() {
        return this.resultColumn;
    }

    public String toString() {
        return "[Collect: id=" + resultColumn.getIdentifier() + "; objectType=" + resultColumn.getObjectType() + "]";
    }

    public void addDescr(PatternDescr patternDescr) {
        // Nothing to do
    }

    public List getDescrs() {
        // nothing to do
        return Collections.EMPTY_LIST;
    }

}
