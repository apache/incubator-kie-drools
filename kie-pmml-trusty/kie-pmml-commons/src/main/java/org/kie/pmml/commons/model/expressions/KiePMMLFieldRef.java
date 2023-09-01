package org.kie.pmml.commons.model.expressions;

import java.util.List;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

import static org.kie.pmml.commons.model.expressions.ExpressionsUtils.getFromPossibleSources;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_FieldRef>FieldRef</a>
 */
public class KiePMMLFieldRef extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private static final long serialVersionUID = 4576394527423997787L;
    private Object mapMissingTo;

    public KiePMMLFieldRef(String name, List<KiePMMLExtension> extensions, Object mapMissingTo) {
        super(name, extensions);
        this.mapMissingTo = mapMissingTo;
    }

    public Object getMapMissingTo() {
        return mapMissingTo;
    }

    @Override
    public Object evaluate(final ProcessingDTO processingDTO) {
        return getFromPossibleSources(name, processingDTO)
                .orElse(mapMissingTo);
    }

    @Override
    public String toString() {
        return "KiePMMLFieldRef{" +
                "mapMissingTo='" + mapMissingTo + '\'' +
                ", extensions=" + extensions +
                ", name='" + name + '\'' +
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
        if (!super.equals(o)) {
            return false;
        }
        KiePMMLFieldRef that = (KiePMMLFieldRef) o;
        return Objects.equals(mapMissingTo, that.mapMissingTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mapMissingTo);
    }

}
