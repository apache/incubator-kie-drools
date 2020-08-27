/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
