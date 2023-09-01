package org.drools.model.codegen.execmodel.generator;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;

public class UnificationTypedExpression extends TypedExpression {

    private Optional<String> unificationVariable;
    private Optional<String> unificationName;

    public UnificationTypedExpression(String unificationVariable, Type type, String name) {
        super(null, type);
        this.unificationVariable = Optional.of(unificationVariable);
        this.unificationName = Optional.of(name);
    }

    public Optional<String> getUnificationVariable() {
        return unificationVariable;
    }

    public Optional<String> getUnificationName() {
        return unificationName;
    }

    @Override
    public boolean isNumberLiteral() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnificationTypedExpression cloneWithNewExpression(Expression newExpression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression uncastExpression() {
        return null;
    }

    @Override
    public String toString() {
        return "UnificationTypedExpression{" +
                ", type=" + getType() +
                ", fieldName='" + getFieldName() + '\'' +
                ", unificationVariable=" + unificationVariable +
                ", unificationName=" + unificationName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnificationTypedExpression that = (UnificationTypedExpression) o;
        return Objects.equals(getType(), that.getType()) &&
                Objects.equals(getFieldName(), that.getFieldName()) &&
                Objects.equals(unificationVariable, that.unificationVariable) &&
                Objects.equals(unificationName, that.unificationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getFieldName(), unificationVariable, unificationName);
    }
}