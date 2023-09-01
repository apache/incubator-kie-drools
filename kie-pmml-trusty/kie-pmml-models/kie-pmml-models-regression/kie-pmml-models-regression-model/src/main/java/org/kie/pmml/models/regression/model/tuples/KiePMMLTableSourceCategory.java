package org.kie.pmml.models.regression.model.tuples;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class to represent a <b>table source/table category</b> tupla
 */
public class KiePMMLTableSourceCategory implements Serializable {

    private static final long serialVersionUID = -8635798961429806015L;
    private final String source;

    private final String category;

    public KiePMMLTableSourceCategory(String source, String category) {
        this.source = source;
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "KiePMMLNameValue{" +
                "name=" + source +
                ", value=" + category +
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
        KiePMMLTableSourceCategory that = (KiePMMLTableSourceCategory) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, category);
    }
}
