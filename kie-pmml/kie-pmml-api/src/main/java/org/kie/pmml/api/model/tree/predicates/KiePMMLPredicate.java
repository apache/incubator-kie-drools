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
import java.util.Map;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.abstracts.KiePMMLExtensioned;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdGroup_PREDICATE>PREDICATE</a>
 */
public abstract class KiePMMLPredicate extends KiePMMLExtensioned {

    private static final long serialVersionUID = -1996390505352151403L;

    protected final String id;

    protected String parentId;

    protected KiePMMLPredicate(String id, List<KiePMMLExtension> extensions) {
        super(extensions);
        this.id = id;
    }

    /**
     * Returns the evaluation of the given <code>values</code> if the current <code>KiePMMLPredicate</code> or one of its
     * child is referred to inside the given <b>values</b>, otherwise <code>false</code>
     * @param values
     * @return
     */
    public abstract boolean evaluate(Map<String, Object> values);

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KiePMMLPredicate that = (KiePMMLPredicate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return parentId != null ? parentId.equals(that.parentId) : that.parentId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        return result;
    }
}
