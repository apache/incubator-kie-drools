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

import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;

/**
 * Adaptor to use RuleModel class in GuidedDecisionTable
 */
public class ConditionCol52FieldConstraintAdaptor extends SingleFieldConstraint {

    private static final long serialVersionUID = 540l;

    private ConditionCol52 condition;

    public ConditionCol52FieldConstraintAdaptor() {
    }

    public ConditionCol52FieldConstraintAdaptor( final ConditionCol52 condition ) {
        PortablePreconditions.checkNotNull( "condition",
                                            condition );
        this.condition = condition;
    }

    @Override
    public boolean isBound() {
        return condition.isBound();
    }

    @Override
    public String getFieldBinding() {
        return condition.getBinding();
    }

    @Override
    public String getFieldName() {
        return condition.getFactField();
    }

    @Override
    public String getFieldType() {
        return condition.getFieldType();
    }

    @Override
    public void setFieldBinding( final String fieldBinding ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNewConnective() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeConnective( final int index ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFieldName( final String fieldName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFieldType( final String fieldType ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParent( final FieldConstraint parent ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId( final String id ) {
        throw new UnsupportedOperationException();
    }

}