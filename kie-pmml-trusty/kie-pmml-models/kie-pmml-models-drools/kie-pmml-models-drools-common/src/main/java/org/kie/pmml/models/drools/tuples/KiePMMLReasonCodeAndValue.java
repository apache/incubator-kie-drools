package org.kie.pmml.models.drools.tuples;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Tupla representing the <b>Reason Code</b> and its computed <b>value</b> as used inside <b>Scorecard</b>
 *
 * @see <a href=http://dmg.org/pmml/v4-4/Scorecard.html#rankinReasongCodes>Ranking Reason Codes</a>
 */
public class KiePMMLReasonCodeAndValue implements Serializable {

    private static final long serialVersionUID = 5978972455322748898L;
    private final String reasonCode;
    private final double value;

    public KiePMMLReasonCodeAndValue(String reasonCode, double value) {
        this.reasonCode = reasonCode;
        this.value = value;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLReasonCodeAndValue.class.getSimpleName() + "[", "]")
                .add("reasonCode='" + reasonCode + "'")
                .add("value=" + value)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLReasonCodeAndValue that = (KiePMMLReasonCodeAndValue) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(reasonCode, that.reasonCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reasonCode, value);
    }
}
