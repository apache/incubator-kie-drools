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

package org.drools.workbench.models.testscenarios.shared;

import java.util.List;

public class FactData
        extends Fact
        implements Fixture {

    private static final long serialVersionUID = 510l;

    /**
     * The name of the "variable"
     */
    private String name;

    /**
     * If its a modify, obviously we are modifying existing data in working memory.
     */
    private boolean isModify;

    public FactData() {
    }

    public FactData( final String type,
                     final String name,
                     final List<Field> fieldData,
                     final boolean modify ) {
        this( type, name, modify );
        this.setFieldData( fieldData );

    }

    public FactData( final String type,
                     final String name,
                     final boolean modify ) {
        super( type );
        this.name = name;
        this.isModify = modify;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setModify( final boolean modify ) {
        isModify = modify;
    }

    public boolean isModify() {
        return isModify;
    }
}
