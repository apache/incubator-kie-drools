package org.kie.pmml.commons.model.predicates;

import java.util.List;
import java.util.Map;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdGroup_PREDICATE>PREDICATE</a>
 */
public abstract class KiePMMLPredicate extends AbstractKiePMMLComponent {

    protected KiePMMLPredicate(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    /**
     * Returns the evaluation of the given <code>values</code> if the current <code>KiePMMLPredicate</code> or one of its
     * child is referred to inside the given <b>values</b>, otherwise <code>false</code>
     * @param values
     * @return
     */
    public abstract boolean evaluate(Map<String, Object> values);
}
