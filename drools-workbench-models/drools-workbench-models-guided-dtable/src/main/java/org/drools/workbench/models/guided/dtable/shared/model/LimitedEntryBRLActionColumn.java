/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.Collections;
import java.util.List;

/**
 * This is the config for a BRLActionColumn that supports Limited Entry. Since a
 * Limited Entry BRLActionColumn contains the literal values in the BRL
 * definition it does not have a discrete value itself.
 */
public class LimitedEntryBRLActionColumn extends BRLActionColumn
        implements
        LimitedEntryCol {

    private static final long serialVersionUID = 540l;

    private static final List<BRLActionVariableColumn> EMPTY_VARIABLES = Collections.emptyList();

    public DTCellValue52 getValue() {
        throw new UnsupportedOperationException( "LimitedEntryBRLActionColumn does not have a value" );
    }

    public void setValue( DTCellValue52 value ) {
        //Silently ignore, otherwise Errai marshalling barfs
    }

    @Override
    public List<BRLActionVariableColumn> getChildColumns() {
        return EMPTY_VARIABLES;
    }

    @Override
    public void setChildColumns( List<BRLActionVariableColumn> childColumns ) {
        //Silently ignore, otherwise Errai marshalling barfs
    }

}
