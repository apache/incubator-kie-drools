package org.kie.pmml.commons.model.expressions;

import java.util.List;
import java.util.Optional;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_DiscretizeBin>DiscretizeBin</a>
 */
public class KiePMMLDiscretizeBin extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -6437255657731885594L;
    private final String binValue;
    private final KiePMMLInterval interval;

    public KiePMMLDiscretizeBin(String name, List<KiePMMLExtension> extensions, String binValue, KiePMMLInterval interval) {
        super(name, extensions);
        this.binValue = binValue;
        this.interval = interval;
    }

    public String getBinValue() {
        return binValue;
    }

    public KiePMMLInterval getInterval() {
        return interval;
    }

    public Optional<String> evaluate(Number toEvaluate) {
        return interval.isIn(toEvaluate) ? Optional.of(binValue) : Optional.empty();
    }
}
