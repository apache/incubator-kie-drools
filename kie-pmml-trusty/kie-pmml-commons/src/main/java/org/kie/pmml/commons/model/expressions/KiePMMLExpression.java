package org.kie.pmml.commons.model.expressions;

import java.io.Serializable;

import org.kie.pmml.commons.model.ProcessingDTO;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Transformations.html#xsdGroup_EXPRESSION>EXPRESSION</a>
 */
public interface KiePMMLExpression extends Serializable {

    Object evaluate(final ProcessingDTO processingDTO);
}
