package org.drools.workbench.models.guided.dtable.shared.model.adaptors;

import org.drools.workbench.models.commons.shared.rule.FieldConstraint;
import org.drools.workbench.models.commons.shared.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.kie.commons.validation.PortablePreconditions;

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