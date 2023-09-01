package org.kie.pmml.models.drools.ast;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;

/**
 * Class representing a <code>Pattern</code> with the referred field, its possible <code>KiePMMLOperatorValue</code>s and, eventually, nested <code>Pattern</code>s
 */
public class KiePMMLFieldOperatorValue {

    public static final String NO_FIELD_CONSTRAINT_PATTERN = "(%s)";
    public static final String FIELD_CONSTRAINT_PATTERN = " %s " + NO_FIELD_CONSTRAINT_PATTERN;
    private final String name;
    private final BOOLEAN_OPERATOR operator;
    private final List<KiePMMLOperatorValue> kiePMMLOperatorValues;
    private final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues;
    private final String constraintsString;

    /**
     * @param name The name of the type
     * @param operator the operator to use to join multiple <code>KiePMMLOperatorValue</code>s (if provided)
     * @param kiePMMLOperatorValues the inner <code>List&lt;KiePMMLOperatorValue&gt;</code>
     * @param nestedKiePMMLFieldOperatorValues the nested <code>List&lt;KiePMMLFieldOperatorValue&gt;</code>s
     */
    public KiePMMLFieldOperatorValue(final String name, final BOOLEAN_OPERATOR operator, final List<KiePMMLOperatorValue> kiePMMLOperatorValues, final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues) {
        this.name = name;
        this.operator = operator;
        this.kiePMMLOperatorValues = kiePMMLOperatorValues;
        this.nestedKiePMMLFieldOperatorValues = nestedKiePMMLFieldOperatorValues;
        constraintsString = buildConstraintsString();
    }

    public String getName() {
        return name;
    }

    public BOOLEAN_OPERATOR getOperator() {
        return operator;
    }

    public String getConstraintsAsString() {
        return constraintsString;
    }

    public List<KiePMMLFieldOperatorValue> getNestedKiePMMLFieldOperatorValues() {
        return nestedKiePMMLFieldOperatorValues != null ? Collections.unmodifiableList(nestedKiePMMLFieldOperatorValues) : null;
    }

    public List<KiePMMLOperatorValue> getKiePMMLOperatorValues() {
        return kiePMMLOperatorValues;
    }

    @Override
    public String toString() {
        return "KiePMMLFieldOperatorValue{" +
                "name='" + name + '\'' +
                ", operator='" + operator + '\'' +
                ", kiePMMLOperatorValues=" + kiePMMLOperatorValues +
                ", nestedKiePMMLFieldOperatorValues=" + nestedKiePMMLFieldOperatorValues +
                ", constraintsString='" + constraintsString + '\'' +
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
        KiePMMLFieldOperatorValue that = (KiePMMLFieldOperatorValue) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(operator, that.operator) &&
                Objects.equals(kiePMMLOperatorValues, that.kiePMMLOperatorValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, operator, kiePMMLOperatorValues);
    }

    protected String buildConstraintsString() {
        String operatorString = operator != null ? operator.getCustomOperator() : "";
        return kiePMMLOperatorValues.stream().map(KiePMMLOperatorValue::getConstraintsAsString).collect(Collectors.joining(" " + operatorString + " "));
    }
}
