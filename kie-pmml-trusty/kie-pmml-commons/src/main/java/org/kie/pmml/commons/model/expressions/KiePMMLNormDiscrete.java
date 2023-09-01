package org.kie.pmml.commons.model.expressions;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

import static org.kie.pmml.commons.model.expressions.ExpressionsUtils.getFromPossibleSources;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_NormDiscrete>NormDiscrete</a>
 */
public class KiePMMLNormDiscrete extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private static final long serialVersionUID = -7935602676734880795L;
    
    private final String value;
    private final Number mapMissingTo;

    public KiePMMLNormDiscrete(final String name,
                               final List<KiePMMLExtension> extensions,
                               final String value,
                               final Number mapMissingTo) {
        super(name, extensions);
        this.value = value;
        this.mapMissingTo = mapMissingTo;
    }

    public String getValue() {
        return value;
    }

    public Number getMapMissingTo() {
        return mapMissingTo;
    }

    @Override
    public Object evaluate(final ProcessingDTO processingDTO) {
        String input = (String) getFromPossibleSources(name, processingDTO)
                .orElse(null);
        if (input == null) {
            return mapMissingTo;
        }
        return input.equals(value) ? 1.0 : 0.0;
    }

}
