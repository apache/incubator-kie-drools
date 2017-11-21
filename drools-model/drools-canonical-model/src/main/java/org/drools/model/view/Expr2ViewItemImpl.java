package org.drools.model.view;

import org.drools.model.BetaIndex;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.index.BetaIndexImpl;

import static org.drools.model.functions.LambdaIntrospector.getLambdaFingerprint;

public class Expr2ViewItemImpl<T, U> extends AbstractExprViewItem<T> implements Expr2ViewItem<T, U> {

    private final Variable<U> var2;
    private final Predicate2<T, U> predicate;

    private BetaIndex<T, U, ?> index;

    public Expr2ViewItemImpl( Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
        super(getLambdaFingerprint(predicate), var1);
        this.var2 = var2;
        this.predicate = predicate;
    }

    public Expr2ViewItemImpl( String exprId, Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
        super(exprId, var1);
        this.var2 = var2;
        this.predicate = predicate;
    }

    public Predicate2<T, U> getPredicate() {
        return predicate;
    }

    public Variable<U> getSecondVariable() {
        return var2;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable(), getSecondVariable() };
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

    public BetaIndex<T, U, ?> getIndex() {
        return index;
    }

    @Override
    public <V> Expr2ViewItemImpl<T, U> indexedBy( Class<?> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, V> rightOperandExtractor ) {
        index = new BetaIndexImpl<T, U, V>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor );
        return this;
    }

}
