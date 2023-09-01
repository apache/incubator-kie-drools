package org.kie.pmml.commons.model.expressions;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_LinearNorm>LinearNorm</a>
 */
public class KiePMMLLinearNorm extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -6437255657731885594L;
    private final double orig;
    private final double norm;

    public KiePMMLLinearNorm(String name, List<KiePMMLExtension> extensions, double orig, double norm) {
        super(name, extensions);
        this.orig = orig;
        this.norm = norm;
    }

    public double getOrig() {
        return orig;
    }

    public double getNorm() {
        return norm;
    }
}
