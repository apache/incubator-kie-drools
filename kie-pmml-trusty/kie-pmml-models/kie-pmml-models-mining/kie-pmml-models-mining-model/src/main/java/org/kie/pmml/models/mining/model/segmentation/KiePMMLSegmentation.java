package org.kie.pmml.models.mining.model.segmentation;

import java.util.List;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;

/**
 * @see <a href=http://dmg.org/pmml/v4-3/MultipleModels.html#xsdElement_Segmentation>Segmentation</a>
 */
public class KiePMMLSegmentation extends AbstractKiePMMLComponent {

    private final MULTIPLE_MODEL_METHOD multipleModelMethod;
    protected List<KiePMMLSegment> segments;

    protected KiePMMLSegmentation(String name, List<KiePMMLExtension> extensions, MULTIPLE_MODEL_METHOD multipleModelMethod) {
        super(name, extensions);
        this.multipleModelMethod = multipleModelMethod;
    }

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(String name, List<KiePMMLExtension> extensions, MULTIPLE_MODEL_METHOD multipleModelMethod) {
        return new Builder(name, extensions, multipleModelMethod);
    }

    public MULTIPLE_MODEL_METHOD getMultipleModelMethod() {
        return multipleModelMethod;
    }

    public List<KiePMMLSegment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return "KiePMMLSegmentation{" +
                "multipleModelMethod=" + multipleModelMethod +
                ", segments=" + segments +
                ", extensions=" + extensions +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
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
        KiePMMLSegmentation that = (KiePMMLSegmentation) o;
        return multipleModelMethod == that.multipleModelMethod &&
                Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), multipleModelMethod, segments);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLSegmentation> {

        private Builder(String name, List<KiePMMLExtension> extensions, MULTIPLE_MODEL_METHOD multipleModelMethod) {
            super("Segmentation-", () -> new KiePMMLSegmentation(name, extensions, multipleModelMethod));
        }

        public Builder withSegments(List<KiePMMLSegment> segments) {
            toBuild.segments = segments;
            return this;
        }
    }
}
