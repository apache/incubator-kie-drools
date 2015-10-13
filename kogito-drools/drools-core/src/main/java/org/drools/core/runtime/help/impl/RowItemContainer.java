/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.runtime.help.impl;

import org.kie.api.runtime.rule.FactHandle;


public class RowItemContainer {

    private String identifier;
    private FactHandle factHandle;
    private Object object;

    public RowItemContainer() {

    }

    public RowItemContainer(String identifier,
                            FactHandle factHandle,
                            Object object) {
        super();
        this.identifier = identifier;
        this.factHandle = factHandle;
        this.object = object;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier( String identifier ) {
        this.identifier = identifier;
    }

    public FactHandle getFactHandle() {
        return factHandle;
    }

    public void setFactHandle(FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
