package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.BooleanExpressionConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Tuple;

public class BooleanConstraint
    implements
    Constraint
{
    private final Declaration                 declaration;

    private final BooleanExpressionConstraint booleanExpression;

    private final Declaration[]               requiredDeclarations;

    public BooleanConstraint(BooleanExpressionConstraint expression,
                             Declaration declaration)
    {
        this( expression,
              declaration,
              null );
    }

    public BooleanConstraint(BooleanExpressionConstraint expression,
                             Declaration declaration,
                             Declaration[] declarations)
    {
        this.booleanExpression = expression;

        this.declaration = declaration;

        if ( declarations == null )
        {
            this.requiredDeclarations = new Declaration[]{};
        }
        else
        {
            this.requiredDeclarations = declarations;
        }
    }

    public BooleanExpressionConstraint getExpression()
    {
        return this.booleanExpression;
    }

    public Declaration[] getRequiredDeclarations()
    {
        return requiredDeclarations;
    }

    public Declaration getDeclaration()
    {
        return this.declaration;
    }

    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple)
    {
        return this.booleanExpression.isAllowed( object,
                                                 handle,
                                                 this.declaration,
                                                 this.requiredDeclarations,
                                                 tuple );
    }

};
