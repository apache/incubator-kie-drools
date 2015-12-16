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

package org.drools.workbench.models.datamodel.oracle;

/**
 * Used to drive drop downs.
 */
public class DropDownData {

    /**
     * If this is non null, just show these items.
     */
    private String[] fixedList = null;

    /**
     * this would be something that takes the name/value pairs and interpolates them into an MVEL expression
     * that resolves to a list.
     */
    private String queryExpression = null;

    /**
     * Something like as list of:
     * sex=M, name=Michael etc....
     */
    private String[] valuePairs = null;

    public DropDownData() {
    }

    public static DropDownData create( String[] list ) {
        if ( list == null ) {
            return null;
        }
        return new DropDownData( list );
    }

    public static DropDownData create( String queryExpression,
                                       String[] valuePairs ) {
        if ( queryExpression == null ) {
            return null;
        }
        return new DropDownData( queryExpression, valuePairs );
    }

    private DropDownData( String[] list ) {
        this.fixedList = list;
    }

    private DropDownData( String queryExpression,
                          String[] valuePairs ) {
        this.queryExpression = queryExpression;
        this.valuePairs = valuePairs;
    }

    public String[] getFixedList() {
        return fixedList;
    }

    public String getQueryExpression() {
        return queryExpression;
    }

    public String[] getValuePairs() {
        return valuePairs;
    }
}
