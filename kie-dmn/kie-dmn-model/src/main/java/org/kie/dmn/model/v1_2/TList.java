package org.kie.dmn.model.v1_2;

import java.util.ArrayList;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.List;

public class TList extends TExpression implements List {

    protected java.util.List<Expression> expression;

    @Override
    public java.util.List<Expression> getExpression() {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

}
