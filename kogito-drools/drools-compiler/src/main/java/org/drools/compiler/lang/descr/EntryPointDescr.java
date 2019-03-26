/*
 * Copyright 2007 Red Hat, Inc. and/or its affiliates.
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
 *
 * Created on Dec 13, 2007
 */
package org.drools.compiler.lang.descr;

import org.drools.core.ruleunit.RuleUnitUtil;

/**
 * An entry point descriptor for facts. This is part of the
 * support to multi-stream concurrent event assertion.
 */
public class EntryPointDescr extends PatternSourceDescr {

    private static final long serialVersionUID = 150l;

    public static final EntryPointDescr RULE_UNIT_ENTRY_POINT_DESCR = new EntryPointDescr( RuleUnitUtil.RULE_UNIT_ENTRY_POINT );

    public EntryPointDescr() {
    }
    
    public EntryPointDescr( String id ) {
        this.setText( id );
    }
    
    public void setEntryId( String id ) {
        this.setText( id );
    }
    
    public String getEntryId() {
        return this.getText();
    }
    
    @Override
    public String toString() {
        return "from entry-point \""+getEntryId()+"\"";
    }

}
