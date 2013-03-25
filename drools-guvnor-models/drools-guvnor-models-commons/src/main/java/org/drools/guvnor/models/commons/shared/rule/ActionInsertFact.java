/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.models.commons.shared.rule;

/**
 * This is used when asserting a new fact.
 */
public class ActionInsertFact extends ActionFieldList {

    private String factType;
    private String boundName;
    private boolean isBound;

    public boolean isBound() {
        return isBound;
    }

    public ActionInsertFact( final String type ) {
        this.factType = type;
    }

    public ActionInsertFact() {
    }

    public String getBoundName() {
        return boundName;
    }

    public void setBoundName( String boundName ) {
        this.boundName = boundName;
        isBound = !( boundName == null || "".equals( boundName ) );
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType( String factType ) {
        this.factType = factType;
    }
}
