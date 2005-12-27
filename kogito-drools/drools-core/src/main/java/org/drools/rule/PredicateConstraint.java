package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.Constraint;
import org.drools.spi.PredicateExpressionConstraint;
import org.drools.spi.Tuple;

public class PredicateConstraint
    implements
    Constraint {
    private final Declaration                   declaration;

    private final PredicateExpressionConstraint booleanExpression;

    private final Declaration[]                 requiredDeclarations;

    private static final Declaration[]          EMPTY_DECLARATIONS = new Declaration[0];

    public PredicateConstraint(PredicateExpressionConstraint expression,
                               Declaration declaration){
        this( expression,
              declaration,
              null );
    }

    public PredicateConstraint(PredicateExpressionConstraint expression,
                               Declaration declaration,
                               Declaration[] declarations){
        this.booleanExpression = expression;

        this.declaration = declaration;

        if ( declarations == null ) {
            this.requiredDeclarations = PredicateConstraint.EMPTY_DECLARATIONS;
        }
        else {
            this.requiredDeclarations = declarations;
        }
    }

    public PredicateExpressionConstraint getExpression(){
        return this.booleanExpression;
    }

    public Declaration[] getRequiredDeclarations(){
        return this.requiredDeclarations;
    }

    public Declaration getDeclaration(){
        return this.declaration;
    }

    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple){
        return this.booleanExpression.isAllowed( object,
                                                 handle,
                                                 this.declaration,
                                                 this.requiredDeclarations,
                                                 tuple );
    }

};
