/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.guided.dtable.shared.model.adaptors;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;

/**
 * Adaptor to use RuleModel class in GuidedDecisionTable
 */
public class ActionInsertFactCol52ActionInsertLogicalFactAdaptor extends ActionInsertLogicalFact {

    private static final long serialVersionUID = 540l;

    private ActionInsertFactCol52 action;

    public ActionInsertFactCol52ActionInsertLogicalFactAdaptor() {
    }

    public ActionInsertFactCol52ActionInsertLogicalFactAdaptor( final ActionInsertFactCol52 action ) {
        PortablePreconditions.checkNotNull( "action",
                                            action );
        this.action = action;
        this.setFactType( action.getFactType() );
        final ActionFieldValue afv = new ActionFieldValue();
        afv.setField( action.getFactField() );
        afv.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        afv.setType( action.getType() );
        super.addFieldValue( afv );
    }

    @Override
    public boolean isBound() {
        return !( action.getBoundName() == null || "".equals( action.getBoundName() ) );
    }

    @Override
    public String getBoundName() {
        return action.getBoundName();
    }

    @Override
    public void setBoundName( final String boundName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeField( final int idx ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFieldValue( final ActionFieldValue val ) {
        throw new UnsupportedOperationException();
    }

}