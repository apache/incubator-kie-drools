/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

/**
 * A Fact Pattern used by the ActionInsertFactFieldsPage Wizard page adding
 * a flag indicating whether the Pattern is inserted Logically or not
 */
@SuppressWarnings("serial")
public class ActionInsertFactFieldsPattern extends Pattern52 {

    private boolean isInsertedLogically;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_IS_INSERTED_LOGICALLY = "isInsertedLogically";

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        ActionInsertFactFieldsPattern other = (ActionInsertFactFieldsPattern) otherColumn;

        // Field: isInsertedLogically.
        if ( this.isInsertedLogically() != other.isInsertedLogically() ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_IS_INSERTED_LOGICALLY,
                                                     this.isInsertedLogically(),
                                                     other.isInsertedLogically() ) );
        }

        return result;
    }

    public boolean isInsertedLogically() {
        return isInsertedLogically;
    }

    public void setInsertedLogically( boolean isInsertedLogically ) {
        this.isInsertedLogically = isInsertedLogically;
    }

    @Override
    public ActionInsertFactFieldsPattern clonePattern() {
        ActionInsertFactFieldsPattern cloned = (ActionInsertFactFieldsPattern) super.clonePattern();
        cloned.setInsertedLogically( isInsertedLogically );
        return cloned;
    }

    @Override
    public void update( Pattern52 other ) {
        super.update( other );
        setInsertedLogically( ( (ActionInsertFactFieldsPattern) other ).isInsertedLogically );
    }
}
