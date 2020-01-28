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
package org.kie.pmml.api.model.tree.predicates;

import java.util.List;

import org.kie.pmml.api.model.KiePMMLExtension;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_False>False</a>
 */
public class KiePMMLFalsePredicate extends KiePMMLPredicate {

    private static final long serialVersionUID = -1996390505352151403L;
   private static final String FALSE = "False";
    private String name;

    public static Builder builder(List<KiePMMLExtension> extensions) {
        return new Builder(extensions);
    }

    public String getName() {
        return name;
    }


    private KiePMMLFalsePredicate(List<KiePMMLExtension> extensions) {
        super(extensions);
        this.name = FALSE;
    }

    public static class Builder {

        private KiePMMLFalsePredicate toBuild;

        private Builder(List<KiePMMLExtension> extensions) {
            this.toBuild = new KiePMMLFalsePredicate(extensions);
        }

        public KiePMMLFalsePredicate build() {
            return toBuild;
        }

    }
}
