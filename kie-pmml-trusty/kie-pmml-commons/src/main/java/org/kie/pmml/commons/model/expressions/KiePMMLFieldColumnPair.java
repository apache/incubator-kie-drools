package org.kie.pmml.commons.model.expressions;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * KiePMML representation of a <b>FieldColumnPair</b>
 */
public class KiePMMLFieldColumnPair extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -5245266051098683475L;
    private final String column;

    public KiePMMLFieldColumnPair(String name, List<KiePMMLExtension> extensions, String column) {
        super(name, extensions);
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
