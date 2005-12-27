package org.drools.rule;

import org.drools.FactHandle;
import org.drools.spi.Constraint;
import org.drools.spi.ConstraintComparator;
import org.drools.spi.LiteralExpressionConstraint;
import org.drools.spi.Tuple;

public class LiteralConstraint
    implements
    Constraint {
    private final LiteralExpressionConstraint literalExpression;

    private final ConstraintComparator        comparator;

    private static final Declaration[]        requiredDeclarations = new Declaration[]{};

    public LiteralConstraint(LiteralExpressionConstraint literalExpression,
                             ConstraintComparator comparator){
        this.literalExpression = literalExpression;

        this.comparator = comparator;
    }

    public LiteralExpressionConstraint getliteralExpression(){
        return this.literalExpression;
    }

    /**
     * Not needed but implemented so we can implement the Constraint interface
     * Just returns an empty static Declaration[]
     * 
     */
    public Declaration[] getRequiredDeclarations(){
        return LiteralConstraint.requiredDeclarations;
    }

    public boolean isAllowed(Object object){
        return this.literalExpression.isAllowed( object,
                                                 this.comparator );
    }

    /**
     * LiteralConstraints are always at the alpha node and thus never have
     * access to the Tuple and the handle is not needed. This mehod is used
     * purely so LiteralConstraint can be used with the same interface as the
     * other Constraint implementations.
     * 
     * @param object
     * @param handle
     * @param tuple
     * @return
     */
    public boolean isAllowed(Object object,
                             FactHandle handle,
                             Tuple tuple){
        return isAllowed( object );
    }
};
