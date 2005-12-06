package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.Constraint;
import org.drools.spi.ConstraintComparator;
import org.drools.spi.ReturnValueExpressionConstraint;
import org.drools.spi.Tuple;

public class ReturnValueConstraint
    implements
    Constraint
{
    private final ReturnValueExpressionConstraint returnValueExpression;

    private final Declaration[]                   requiredDeclarations;

    private final ConstraintComparator            comparator;
    
    private static final Declaration[]           noRequiredDeclarations = new Declaration[] {};

    public ReturnValueConstraint(ReturnValueExpressionConstraint expression,
                                 Declaration[] declarations,
                                 ConstraintComparator comparator)
    {
        this.returnValueExpression = expression;

        if ( declarations != null )
        {
            this.requiredDeclarations = declarations;
        }
        else
        {
            this.requiredDeclarations = noRequiredDeclarations;
        }

        this.comparator = comparator;
    }

    public ReturnValueExpressionConstraint getExpression()
    {
        return this.returnValueExpression;
    }

    public Declaration[] getRequiredDeclarations()
    {
        return this.requiredDeclarations;
    }

    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple)
    {
        return this.returnValueExpression.isAllowed( object,
                                                     handle,
                                                     this.requiredDeclarations,
                                                     tuple,
                                                     this.comparator );
    }
};
