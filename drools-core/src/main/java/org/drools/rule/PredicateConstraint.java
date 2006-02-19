package org.drools.rule;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;

public class PredicateConstraint
    implements
    FieldConstraint {
    
    private PredicateExpression        expression;
    
    private final Declaration          declaration;

    private final Declaration[]        requiredDeclarations;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];

    public PredicateConstraint(PredicateExpression evaluator,
                               Declaration declaration) {
        this( evaluator,
              declaration,
              null );
    }
    
    public PredicateConstraint(Declaration declaration,
                               Declaration[] requiredDeclarations) {
        this ( null, declaration, requiredDeclarations );
    }    
    

    public PredicateConstraint(PredicateExpression expression,
                               Declaration declaration,
                               Declaration[] requiredDeclarations) {

        this.expression = expression;

        this.declaration = declaration;

        if ( requiredDeclarations == null ) {
            this.requiredDeclarations = PredicateConstraint.EMPTY_DECLARATIONS;
        } else {
            this.requiredDeclarations = requiredDeclarations;
        }
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public void setPredicateExpression(PredicateExpression expression) {
        this.expression = expression;
    }

    public boolean isAllowed(FactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {

        return expression.evaluate( tuple,
                                    handle,
                                    this.declaration,
                                    this.requiredDeclarations,
                                    workingMemory );

    }

};
