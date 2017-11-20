package org.drools.model.view;

import org.drools.model.AlphaIndex;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.index.AlphaIndexImpl;

public class Expr1ViewItemImpl<T> extends AbstractExprViewItem<T> implements Expr1ViewItem<T> {

    private final Predicate1<T> predicate;

    private AlphaIndex<T, ?> index;

    public Expr1ViewItemImpl( Variable<T> var, Predicate1<T> predicate ) {
        super(predicate.toString(), var);
        this.predicate = predicate;
    }

    public Expr1ViewItemImpl( String exprId, Variable<T> var, Predicate1<T> predicate ) {
        super(exprId, var);
        this.predicate = predicate;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable() };
    }

    public Predicate1<T> getPredicate() {
        return predicate;
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

    public AlphaIndex<T, ?> getIndex() {
        return index;
    }

    @Override
    public <U> Expr1ViewItemImpl<T> indexedBy( Class<?> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, U> leftOperandExtractor, U rightValue ) {
        index = new AlphaIndexImpl<T, U>( indexedClass, constraintType, indexId, leftOperandExtractor, rightValue);
        return this;
    }
}
