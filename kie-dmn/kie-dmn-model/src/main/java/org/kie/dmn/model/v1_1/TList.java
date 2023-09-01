package org.kie.dmn.model.v1_1;

import java.util.ArrayList;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.List;

/**
 * Representation for DMN XSD tList type.
 */
public class TList extends TExpression implements List {

    private java.util.List<Expression> expression;

    @Override
    public java.util.List<Expression> getExpression() {
        if ( expression == null ) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

}
